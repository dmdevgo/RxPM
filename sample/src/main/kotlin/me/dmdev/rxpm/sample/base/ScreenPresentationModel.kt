package me.dmdev.rxpm.sample.base

import io.reactivex.functions.Consumer
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.sample.BackMessage
import me.dmdev.rxpm.sample.PmMessage
import me.dmdev.rxpm.sample.UpMessage

abstract class ScreenPresentationModel : PresentationModel() {

    val messages = Command<PmMessage>()

    open val upAction = Consumer<Unit> { sendMessage(UpMessage()) }
    open val backAction = Consumer<Unit> { sendMessage(BackMessage()) }

    protected fun sendMessage(message: PmMessage) {
        messages.consumer.accept(message)
    }
}