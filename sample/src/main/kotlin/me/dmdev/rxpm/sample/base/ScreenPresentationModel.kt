package me.dmdev.rxpm.sample.base

import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.sample.BackMessage
import me.dmdev.rxpm.sample.PmMessage
import me.dmdev.rxpm.sample.UpMessage

/**
 * @author Dmitriy Gorbunov
 */
abstract class ScreenPresentationModel : PresentationModel() {

    val messages = Command<PmMessage>()

    private val upActionDefault = Action<Unit>()
    private val backActionDefault = Action<Unit>()

    open val upAction: Action<Unit>  = upActionDefault
    open val backAction: Action<Unit>  = backActionDefault

    override fun onCreate() {
        super.onCreate()

        upActionDefault.observable
                .subscribe { sendMessage(UpMessage()) }
                .untilDestroy()

        backActionDefault.observable
                .subscribe { sendMessage(BackMessage()) }
                .untilDestroy()
    }

    protected fun sendMessage(message: PmMessage) {
        messages.consumer.accept(message)
    }
}