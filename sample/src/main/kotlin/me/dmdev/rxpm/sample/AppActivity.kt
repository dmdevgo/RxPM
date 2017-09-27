package me.dmdev.rxpm.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import me.dmdev.rxpm.sample.extensions.back
import me.dmdev.rxpm.sample.extensions.currentScreen
import me.dmdev.rxpm.sample.extensions.findScreen
import me.dmdev.rxpm.sample.extensions.openScreen
import me.dmdev.rxpm.sample.ui.base.BackHandler
import me.dmdev.rxpm.sample.ui.country.ChooseCountryScreen
import me.dmdev.rxpm.sample.ui.phone.AuthByPhoneScreen

/**
 * @author Dmitriy Gorbunov
 */

class AppActivity : AppCompatActivity(), NavigationMessageHandler {

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
        val sfm = supportFragmentManager
        when (message) {
            is UpMessage,
            is BackMessage -> super.onBackPressed()

            is ChooseCountryMessage -> sfm.openScreen(ChooseCountryScreen())

            is CountryChosenMessage -> {
                sfm.findScreen<AuthByPhoneScreen>()?.onCountryChosen(message.country)
                sfm.back()
            }
        }
        return true
    }
}