package me.dmdev.rxpm.sample.main.ui.base

import me.dmdev.rxpm.*
import me.dmdev.rxpm.navigation.*
import me.dmdev.rxpm.sample.main.*
import me.dmdev.rxpm.widget.*


abstract class ScreenPresentationModel : PresentationModel() {

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