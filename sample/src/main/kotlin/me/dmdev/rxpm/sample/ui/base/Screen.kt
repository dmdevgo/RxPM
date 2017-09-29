package me.dmdev.rxpm.sample.ui.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.functions.Consumer
import me.dmdev.rxpm.base.PmSupportFragment
import me.dmdev.rxpm.sample.NavigationMessage
import me.dmdev.rxpm.sample.NavigationMessageHandler
import me.dmdev.rxpm.sample.extensions.findScreen
import me.dmdev.rxpm.sample.extensions.showDialog

/**
 * @author Dmitriy Gorbunov
 */
abstract class Screen<PM : ScreenPresentationModel> : PmSupportFragment<PM>(),
                                                      NavigationMessageHandler,
                                                      BackHandler {

    abstract val screenLayout: Int

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(screenLayout, container, false)
    }

    override fun onBindPresentationModel(pm: PM) {
        pm.messages.observable.bindTo { dispatchMessage(it) }
        pm.errors.observable.bindTo { showError(it) }
    }

    private fun showError(errorMessage: String) {
        childFragmentManager.showDialog(
                MessageDialog.newInstance(errorMessage),
                tag = "error_message"
        )
    }

    override fun handleBack(): Boolean {
        presentationModel.backAction.consumer.accept(Unit)
        return true
    }

    override fun handleMessage(message: NavigationMessage) = false

    private fun dispatchMessage(message: NavigationMessage) {

        var handler: Fragment? = this
        do {
            if (handler is NavigationMessageHandler && handler.handleMessage(message)) {
                return
            }
            handler = handler?.parentFragment
        } while (handler != null)

        val ac = activity
        if (ac is NavigationMessageHandler) {
            ac.handleMessage(message)
        }
    }

    val progressConsumer = Consumer<Boolean> {
        if (it) {
            childFragmentManager.showDialog(ProgressDialog())
        } else {
            childFragmentManager
                    .findScreen<ProgressDialog>()
                    ?.dismiss()
        }
    }
}