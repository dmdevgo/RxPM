package me.dmdev.rxpm.sample.main.ui.base

import io.reactivex.functions.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.navigation.*
import me.dmdev.rxpm.sample.main.AppNavigationMessage.*
import me.dmdev.rxpm.widget.*


abstract class ScreenPresentationModel : PresentationModel(), NavigationalPm {

    override val navigationMessages = command<NavigationMessage>()

    val errorDialog = dialogControl<String, Unit>()

    protected val errorConsumer = Consumer<Throwable?> {
        errorDialog.show(it?.message ?: "Unknown error")
    }

    open val backAction = action<Unit> {
        this.map { Back }
            .doOnNext(navigationMessages.consumer)
    }

    protected fun sendMessage(message: NavigationMessage) {
        navigationMessages.accept(message)
    }

    protected fun showError(errorMessage: String?) {
        errorDialog.show(errorMessage ?: "Unknown error")
    }
}