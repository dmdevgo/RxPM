package me.dmdev.rxpm

import io.reactivex.Maybe
import io.reactivex.Single
import me.dmdev.rxpm.permission.PermissionResults
import me.dmdev.rxpm.permission.areGranted
import me.dmdev.rxpm.permission.isPermanentlyDeclined
import me.dmdev.rxpm.permission.isRationaleRequired
import me.dmdev.rxpm.widget.dialogControl

// TODO: comments & tests
class Permission internal constructor(
        private val pm: PresentationModel,
        private val permissions: Array<out String>,
        private val requestCode: Int = 1001,
        private val useRationaleDialog: Boolean
) {

    enum class RationaleAction {
        ALLOW, CANCEL
    }

    val rationale = pm.dialogControl<PermissionResults, RationaleAction>()

    val permanentlyDenied = pm.command<PermissionResults>(bufferSize = 1)

    private var requestInProgress = false

    internal fun request(): Single<Boolean> {
        return pm.unbind
            .switchMapMaybe { unbound ->
                // If PM is unbound we must dispose current request if any.
                // It's necessary to stop using PermissionDelegate to prevent memory leak.
                Maybe.just(!unbound)
                    .filter { it }
                    .flatMap { requestPermissions().toMaybe() }
            }
            .take(1)
            .singleOrError()
    }

    private fun requestPermissions(): Single<Boolean> {
        val currentResults = pm.permissionDelegate!!.checkPermissions(permissions)
        if (currentResults.areGranted) {
            return Single.just(true)
        }
        val shouldRequestPermissions = useRationaleDialog
            .takeIf { it && !requestInProgress }
            ?.let { currentResults.filter { it.isRationaleRequired } }
            ?.takeIf { it.isNotEmpty() }
            ?.let { list ->
                rationale.showForResult(list)
                    .map { it == RationaleAction.ALLOW }
                    .toSingle(false)
            }
            ?: Single.just(true)

        return shouldRequestPermissions
            .flatMap { shouldRequest ->
                if (shouldRequest) {
                    pm.permissionDelegate!!.requestPermissions(permissions, requestCode)
                        .doOnSubscribe { requestInProgress = true }
                        .doOnSuccess { requestInProgress = false }
                        .map { results ->
                            val allGranted = results.areGranted
                            if (!allGranted) {
                                val permanentlyDenied = results.filter { it.isPermanentlyDeclined }
                                if (permanentlyDenied.isNotEmpty()) {
                                    this.permanentlyDenied.relay.accept(permanentlyDenied)
                                }
                            }
                            allGranted
                        }
                } else {
                    Single.just(false)
                }
            }
    }

}

fun PresentationModel.permission(
        vararg permissions: String,
        requestCode: Int = 1001,
        useRationaleDialog: Boolean = false
): Permission {
    return Permission(this, permissions, requestCode, useRationaleDialog)
}