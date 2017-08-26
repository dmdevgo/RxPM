package me.dmdev.rxpm.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import me.dmdev.rxpm.sample.base.BackHandler
import me.dmdev.rxpm.sample.extansions.currentScreen
import me.dmdev.rxpm.sample.extansions.openScreen
import me.dmdev.rxpm.sample.ui.auth.phone.AuthByPhoneScreen

class MainActivity : AppCompatActivity(), PmMessageHandler {

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

    override fun handleMessage(message: PmMessage): Boolean {
        when (message) {
            is UpMessage,
            is BackMessage -> super.onBackPressed()
        }
        return true
    }
}

