package me.dmdev.rxpm.sample.ui.main

import io.reactivex.android.schedulers.AndroidSchedulers
import me.dmdev.rxpm.bindProgress
import me.dmdev.rxpm.sample.LogoutCompletedMessage
import me.dmdev.rxpm.sample.model.AuthModel
import me.dmdev.rxpm.sample.ui.base.ScreenPresentationModel
import me.dmdev.rxpm.skipWhileInProgress
import me.dmdev.rxpm.widget.dialogControl

class MainPm(
        private val authModel: AuthModel
) : ScreenPresentationModel() {

    sealed class DialogResult {
        object Ok : DialogResult()
        object Cancel : DialogResult()
    }

    val logoutDialog = dialogControl<Unit, DialogResult>()
    val inProgress = State(false)

    val logoutAction = Action<Unit>()

    override fun onCreate() {
        super.onCreate()

        logoutAction.observable
                .skipWhileInProgress(inProgress.observable)
                .switchMapMaybe {
                    logoutDialog.showForResult(Unit)
                            .filter { it == DialogResult.Ok }
                }
                .switchMapCompletable {
                    authModel.logout()
                            .observeOn(AndroidSchedulers.mainThread())
                            .bindProgress(inProgress.consumer)
                            .doOnError { showError(it.message) }
                            .doOnComplete { sendMessage(LogoutCompletedMessage()) }
                }
                .retry()
                .subscribe()
                .untilDestroy()
    }
}