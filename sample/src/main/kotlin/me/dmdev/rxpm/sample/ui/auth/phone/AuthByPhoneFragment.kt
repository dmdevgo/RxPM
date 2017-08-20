package me.dmdev.rxpm.sample.ui.auth.phone

import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.base.BaseFragment

class AuthByPhoneFragment : BaseFragment<AuthByPhonePm>() {

    override fun getFragmentLayout() = R.layout.fragment_auth_by_phone

    override fun providePresentationModel() = AuthByPhonePm()

    override fun onBindPresentationModel(pm: AuthByPhonePm) {}

}

