package me.dmdev.rxpm.sample.main.ui.main

import me.dmdev.rxpm.*
import me.dmdev.rxpm.sample.main.AppNavigationMessage.*
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

    val logoutClicks = action<Unit> {
        this.skipWhileInProgress(inProgress)
            .switchMapMaybe {
                logoutDialog.showForResult(Unit)
                    .filter { it == DialogResult.Ok }
            }
            .switchMapCompletable {
                authModel.logout()
                    .bindProgress(inProgress)
                    .doOnError { showError(it.message) }
                    .doOnComplete { sendMessage(LogoutCompleted) }
            }
            .toObservable<Any>()
    }
}