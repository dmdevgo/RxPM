package me.dmdev.rxpm.sample.main.ui.main

import me.dmdev.rxpm.*
import me.dmdev.rxpm.sample.main.*
import me.dmdev.rxpm.sample.main.model.*
import me.dmdev.rxpm.sample.main.ui.base.*
import me.dmdev.rxpm.widget.*

class MainPm(private val authModel: AuthModel) : ScreenPresentationModel() {

    sealed class DialogResult {
        object Ok : DialogResult()
        object Cancel : DialogResult()
    }

    val logoutDialog = dialogControl<Unit, DialogResult>()
    val inProgress = state(false)

    val logoutAction = action<Unit>()

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
                    .bindProgress(inProgress.consumer)
                    .doOnError { showError(it.message) }
                    .doOnComplete { sendMessage(LogoutCompletedMessage()) }
            }
            .retry()
            .subscribe()
            .untilDestroy()
    }
}