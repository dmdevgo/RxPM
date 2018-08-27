package me.dmdev.rxpm.sample.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import me.dmdev.rxpm.navigation.NavigationMessage
import me.dmdev.rxpm.navigation.NavigationMessageHandler
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.main.extensions.*
import me.dmdev.rxpm.sample.main.ui.base.BackHandler
import me.dmdev.rxpm.sample.main.ui.confirmation.CodeConfirmationScreen
import me.dmdev.rxpm.sample.main.ui.country.ChooseCountryScreen
import me.dmdev.rxpm.sample.main.ui.main.MainScreen
import me.dmdev.rxpm.sample.main.ui.phone.AuthByPhoneScreen


class MainActivity : AppCompatActivity(), NavigationMessageHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.openScreen(AuthByPhoneScreen(), addToBackStack = false)
        }
    }

    override fun onBackPressed() {
        supportFragmentManager.currentScreen?.let {
            if (it is BackHandler && it.handleBack()) return
        }

        if (supportFragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
        }
    }

    override fun handleNavigationMessage(message: NavigationMessage): Boolean {

        val sfm = supportFragmentManager

        when (message) {

            is BackMessage -> super.onBackPressed()

            is ChooseCountryMessage -> sfm.openScreen(ChooseCountryScreen())

            is CountryChosenMessage -> {
                sfm.findScreen<AuthByPhoneScreen>()?.onCountryChosen(message.country)
                sfm.back()
            }

            is PhoneSentSuccessfullyMessage -> sfm.openScreen(
                CodeConfirmationScreen.newInstance(message.phone)
            )

            is PhoneConfirmedMessage -> {
                sfm.clearBackStack()
                sfm.openScreen(MainScreen(), addToBackStack = false)
            }

            is LogoutCompletedMessage -> {
                sfm.clearBackStack()
                sfm.openScreen(AuthByPhoneScreen(), addToBackStack = false)
            }
        }

        return true
    }
}