package me.dmdev.rxpm.sample.main.ui.base

import me.dmdev.rxpm.Action
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.action
import me.dmdev.rxpm.command
import me.dmdev.rxpm.navigation.NavigationMessage
import me.dmdev.rxpm.navigation.NavigationalPm
import me.dmdev.rxpm.sample.main.BackMessage
import me.dmdev.rxpm.widget.dialogControl


abstract class ScreenPresentationModel : PresentationModel(),
    NavigationalPm {

    override val navigationMessages = command<NavigationMessage>()

    val errorDialog = dialogControl<String, Unit>()

    private val backActionDefault = action<Unit>()

    open val backAction: Action<Unit> = backActionDefault

    override fun onCreate() {
        super.onCreate()

        backActionDefault.observable
            .subscribe { sendMessage(BackMessage()) }
            .untilDestroy()
    }

    protected fun sendMessage(message: NavigationMessage) {
        navigationMessages.consumer.accept(message)
    }

    protected fun showError(errorMessage: String?) {
        errorDialog.show(errorMessage ?: "Unknown error")
    }
}