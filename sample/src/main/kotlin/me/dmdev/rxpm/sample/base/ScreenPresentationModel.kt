package me.dmdev.rxpm.sample.base

import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.sample.BackMessage
import me.dmdev.rxpm.sample.NavigationMessage

/**
 * @author Dmitriy Gorbunov
 */
abstract class ScreenPresentationModel : PresentationModel() {

    val messages = Command<NavigationMessage>()

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
}