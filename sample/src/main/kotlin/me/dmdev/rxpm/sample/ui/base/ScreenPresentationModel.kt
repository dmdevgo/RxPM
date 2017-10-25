package me.dmdev.rxpm.sample.ui.base

import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.sample.BackMessage
import me.dmdev.rxpm.sample.NavigationMessage


abstract class ScreenPresentationModel : PresentationModel() {

    val messages = Command<NavigationMessage>()
    val errors = Command<String>()

    private val backActionDefault = Action<Unit>()

    open val backAction: Action<Unit>  = backActionDefault

    override fun onCreate() {
        super.onCreate()

        backActionDefault.observable
                .subscribe { sendMessage(BackMessage()) }
                .untilDestroy()
    }

    protected fun sendMessage(message: NavigationMessage) {
        messages.consumer.accept(message)
    }

    protected fun showError(error: String?) {
        errors.consumer.accept(error ?: "Unknown error")
    }
}