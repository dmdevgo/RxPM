package me.dmdev.rxpm.sample.main

import android.os.*
import android.support.v7.app.*
import me.dmdev.rxpm.navigation.*
import me.dmdev.rxpm.sample.*
import me.dmdev.rxpm.sample.main.extensions.*
import me.dmdev.rxpm.sample.main.ui.base.*
import me.dmdev.rxpm.sample.main.ui.confirmation.*
import me.dmdev.rxpm.sample.main.ui.country.*
import me.dmdev.rxpm.sample.main.ui.main.*
import me.dmdev.rxpm.sample.main.ui.phone.*


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
                sfm.back()
                sfm.findScreen<AuthByPhoneScreen>()?.onCountryChosen(message.country)
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