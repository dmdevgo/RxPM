package me.dmdev.rxpm.permission

import android.content.pm.PackageManager
import android.util.SparseArray
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.Single

// TODO: comments & tests
class PermissionDelegate(private val caller: PermissionCaller) {

    private val asyncResults = BehaviorRelay.createDefault(SparseArray<PermissionResults>())

    fun checkPermissions(permissions: Array<out String>): PermissionResults {
        return permissions
            .map { permission ->
                val isGranted = caller.isPermissionGranted(permission)
                PermissionResult(permission, getPermissionResultType(permission, isGranted, false))
            }
    }

    fun requestPermissions(permissions: Array<out String>, requestCode: Int): Single<PermissionResults> {
        asyncResults.value?.delete(requestCode)
        caller.requestPermissions(permissions, requestCode)
        return asyncResults
            .switchMap { arr ->
                arr[requestCode]
                    ?.let { Observable.just(it) }
                    ?: Observable.empty<PermissionResults>()
            }
            .take(1)
            .singleOrError()
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (permissions.isEmpty()) return // happens on orientation change

        val results = permissions.mapIndexed { index, permission ->
            val isGranted = grantResults[index] == PackageManager.PERMISSION_GRANTED
            PermissionResult(permission, getPermissionResultType(permission, isGranted, true))
        }

        val arr = this.asyncResults.value!!
        arr.put(requestCode, results)
        this.asyncResults.accept(arr)
    }

    private fun getPermissionResultType(permission: String,
                                        isGranted: Boolean,
                                        requestWasMade: Boolean): PermissionResultType {
        if (isGranted) {
            return PermissionResultType.GRANTED
        }

        val shouldShowRationale = caller.shouldShowRequestPermissionRationale(permission)
        return when {
            !requestWasMade && !shouldShowRationale -> PermissionResultType.UNKNOWN
            !requestWasMade && shouldShowRationale -> PermissionResultType.RATIONALE_REQUIRED
            requestWasMade && !shouldShowRationale -> PermissionResultType.PERMANENTLY_DECLINED
            else -> PermissionResultType.DECLINED
        }
    }
}