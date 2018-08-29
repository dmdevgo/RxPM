package me.dmdev.rxpm.sample.main.ui.confirmation

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import com.jakewharton.rxbinding2.support.v7.widget.navigationClicks
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.editorActions
import io.reactivex.Observable
import kotlinx.android.synthetic.main.screen_code_confirmation.*
import me.dmdev.rxpm.sample.App
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.main.extensions.showKeyboard
import me.dmdev.rxpm.sample.main.ui.base.Screen


class CodeConfirmationScreen : Screen<CodeConfirmationPm>() {

    companion object {
        private const val ARG_PHONE = "arg_phone"
        fun newInstance(phone: String) = CodeConfirmationScreen().apply {
            arguments = Bundle().apply {
                putString(ARG_PHONE, phone)
            }
        }
    }

    override val screenLayout = R.layout.screen_code_confirmation

    override fun providePresentationModel(): CodeConfirmationPm {
        return CodeConfirmationPm(
            arguments!!.getString(ARG_PHONE),
            App.component.resourceProvider,
            App.component.authModel
        )
    }

    override fun onBindPresentationModel(pm: CodeConfirmationPm) {
        super.onBindPresentationModel(pm)

        pm.code bindTo codeEditLayout
        pm.inProgress bindTo progressConsumer
        pm.sendButtonEnabled bindTo sendButton::setEnabled

        toolbar.navigationClicks() bindTo pm.backAction

        Observable
            .merge(
                sendButton.clicks(),
                codeEdit.editorActions()
                    .filter { it == EditorInfo.IME_ACTION_SEND }
                    .map { Unit }
            )
            .bindTo(pm.sendAction)

    }

    override fun onResume() {
        super.onResume()
        codeEdit.showKeyboard()
    }

}
