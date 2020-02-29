package me.dmdev.rxpm.sample.main.ui.phone

import android.view.inputmethod.*
import com.jakewharton.rxbinding3.view.*
import com.jakewharton.rxbinding3.widget.*
import io.reactivex.*
import kotlinx.android.synthetic.main.screen_auth_by_phone.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.sample.*
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.main.extensions.*
import me.dmdev.rxpm.sample.main.ui.base.*
import me.dmdev.rxpm.sample.main.util.*
import me.dmdev.rxpm.widget.*


class AuthByPhoneScreen : Screen<AuthByPhonePm>() {

    override val screenLayout = R.layout.screen_auth_by_phone

    override fun providePresentationModel(): AuthByPhonePm {
        return AuthByPhonePm(
            App.component.phoneUtil,
            App.component.resourceProvider,
            App.component.authModel
        )
    }

    override fun onBindPresentationModel(pm: AuthByPhonePm) {
        super.onBindPresentationModel(pm)

        pm.countryCode bindTo editCountryCodeLayout
        pm.phoneNumber bindTo editPhoneNumberLayout
        pm.chosenCountry bindTo { countryName.text = it.name }

        pm.inProgress bindTo progressConsumer
        pm.sendButtonEnabled bindTo sendButton::setEnabled

        countryName.clicks() bindTo pm.countryClicks

        Observable
            .merge(
                sendButton.clicks(),
                phoneNumberEdit.editorActions()
                    .filter { it == EditorInfo.IME_ACTION_SEND }
                    .map { Unit }
            )
            .bindTo(pm.sendClicks)

    }

    fun onCountryChosen(country: Country) {
        country passTo presentationModel.chooseCountry
    }

    override fun onResume() {
        super.onResume()
        phoneNumberEdit.showKeyboard()
    }
}