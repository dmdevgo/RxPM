package me.dmdev.rxpm.sample.ui.auth.phone

import kotlinx.android.synthetic.main.fragment_auth_by_phone.*
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.base.BaseFragment
import java.util.*

class AuthByPhoneFragment : BaseFragment<AuthByPhonePm>() {

    override fun getFragmentLayout() = R.layout.fragment_auth_by_phone

    override fun providePresentationModel() = AuthByPhonePm()

    override fun onBindPresentationModel(pm: AuthByPhonePm) {
        pm.countryCode.bindTo(editCountryCodeLayout)
        pm.phoneNumber.bindTo(editPhoneNumberLayout)
        pm.doneButton.bindTo(doneButton)
        pm.chosenCountry.observable.bindTo {
            countryName.text = Locale("en", it.region).getDisplayCountry(Locale.ENGLISH)
        }
    }

}

