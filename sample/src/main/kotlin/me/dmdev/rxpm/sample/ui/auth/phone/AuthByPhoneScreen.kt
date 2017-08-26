package me.dmdev.rxpm.sample.ui.auth.phone

import kotlinx.android.synthetic.main.screen_auth_by_phone.*
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.base.Screen
import java.util.*



class AuthByPhoneScreen : Screen<AuthByPhonePm>() {

    override fun getScreenLayout() = R.layout.screen_auth_by_phone

    override fun providePresentationModel() = AuthByPhonePm()

    override fun onBindPresentationModel(pm: AuthByPhonePm) {
        super.onBindPresentationModel(pm)
        pm.countryCode.bindTo(editCountryCodeLayout)
        pm.phoneNumber.bindTo(editPhoneNumberLayout)
        pm.doneButton.bindTo(doneButton)
        pm.chosenCountry.observable.bindTo {
            countryName.text = Locale("en", it.region).getDisplayCountry(Locale.ENGLISH)
        }
    }
}