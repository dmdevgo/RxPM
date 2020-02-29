package me.dmdev.rxpm.sample.main.ui.confirmation

import android.os.*
import android.view.inputmethod.*
import com.jakewharton.rxbinding3.appcompat.*
import com.jakewharton.rxbinding3.view.*
import com.jakewharton.rxbinding3.widget.*
import io.reactivex.*
import kotlinx.android.synthetic.main.screen_code_confirmation.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.sample.*
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.main.extensions.*
import me.dmdev.rxpm.sample.main.ui.base.*
import me.dmdev.rxpm.widget.*


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
            arguments!!.getString(ARG_PHONE)!!,
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
            .bindTo(pm.sendClicks)

    }

    override fun onResume() {
        super.onResume()
        codeEdit.showKeyboard()
    }

}
