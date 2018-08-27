package me.dmdev.rxpm.sample.main.ui.phone

import android.view.inputmethod.EditorInfo
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.editorActions
import io.reactivex.Observable
import kotlinx.android.synthetic.main.screen_auth_by_phone.*
import me.dmdev.rxpm.sample.App
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.main.extensions.showKeyboard
import me.dmdev.rxpm.sample.main.ui.base.Screen
import me.dmdev.rxpm.sample.main.util.Country


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
        pm.phoneNumberFocus bindTo { phoneNumberEdit.requestFocus() }

        countryName.clicks() bindTo pm.countryClicks

        Observable
            .merge(
                sendButton.clicks(),
                phoneNumberEdit.editorActions()
                    .filter { it == EditorInfo.IME_ACTION_SEND }
                    .map { Unit }
            )
            .bindTo(pm.sendAction)

    }

    fun onCountryChosen(country: Country) {
        country passTo presentationModel.chooseCountryAction
    }

    override fun onResume() {
        super.onResume()
        phoneNumberEdit.showKeyboard()
    }
}