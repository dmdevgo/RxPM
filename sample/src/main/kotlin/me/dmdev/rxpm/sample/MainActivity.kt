package me.dmdev.rxpm.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import me.dmdev.rxpm.sample.base.BackHandler
import me.dmdev.rxpm.sample.extansions.currentScreen
import me.dmdev.rxpm.sample.extansions.openScreen
import me.dmdev.rxpm.sample.ui.auth.phone.AuthByPhoneScreen
import me.dmdev.rxpm.sample.ui.country.ChooseCountryScreen

/**
 * @author Dmitriy Gorbunov
 */

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
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun handleMessage(message: NavigationMessage): Boolean {
        when (message) {
            is UpMessage,
            is BackMessage -> super.onBackPressed()

            is ChooseCountryMessage -> supportFragmentManager.openScreen(ChooseCountryScreen())

            is CountryChosenMessage -> {
                (supportFragmentManager.findFragmentByTag(AuthByPhoneScreen::class.java.name)
                        as? AuthByPhoneScreen)?.chosenCountry(message.country)

                super.onBackPressed()
            }
        }
        return true
    }
}
