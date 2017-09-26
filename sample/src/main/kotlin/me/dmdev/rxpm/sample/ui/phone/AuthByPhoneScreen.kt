package me.dmdev.rxpm.sample.ui.phone

import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.screen_auth_by_phone.*
import me.dmdev.rxpm.sample.App
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.ui.base.Screen
import me.dmdev.rxpm.sample.util.Country

/**
 * @author Dmitriy Gorbunov
 */

class AuthByPhoneScreen : Screen<AuthByPhonePm>() {

    override val screenLayout = R.layout.screen_auth_by_phone

    override fun providePresentationModel() = AuthByPhonePm(App.component.phoneUtil())

    override fun onBindPresentationModel(pm: AuthByPhonePm) {
        super.onBindPresentationModel(pm)
        pm.countryCode.bindTo(editCountryCodeLayout)
        pm.phoneNumber.bindTo(editPhoneNumberLayout)
        pm.doneButton.bindTo(doneButton)
        pm.chosenCountry.observable.bindTo {
            countryName.text = it.name
        }

        countryName.clicks().bindTo(pm.countryClicks.consumer)

    }

    fun chosenCountry(country: Country) {
        presentationModel.chooseCountryAction.consumer.accept(country)
    }

}