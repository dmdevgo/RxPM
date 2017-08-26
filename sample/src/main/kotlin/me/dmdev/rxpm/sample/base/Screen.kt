package me.dmdev.rxpm.sample.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.dmdev.rxpm.base.PmSupportFragment
import me.dmdev.rxpm.sample.PmMessage
import me.dmdev.rxpm.sample.PmMessageHandler

/**
 * @author Dmitriy Gorbunov
 */
abstract class Screen<PM : ScreenPresentationModel> : PmSupportFragment<PM>(),
                                                      PmMessageHandler,
                                                      BackHandler {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getScreenLayout(), container, false)
    }

    override fun onBindPresentationModel(pm: PM) {
        pm.messages.observable.bindTo { dispatchMessage(it) }
    }

    override fun handleBack(): Boolean {
        presentationModel.backAction.accept(Unit)
        return true
    }

    override fun handleMessage(message: PmMessage) = false

    private fun dispatchMessage(message: PmMessage) {

        var handler: Fragment? = this
        do {
            if (handler is PmMessageHandler && handler.handleMessage(message)) {
                return
            }
            handler = handler?.parentFragment
        } while (handler != null)

        val ac = activity
        if (ac is PmMessageHandler) {
            ac.handleMessage(message)
        }
    }

    @LayoutRes protected abstract fun getScreenLayout(): Int

}