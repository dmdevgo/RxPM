package me.dmdev.rxpm.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import me.dmdev.rxpm.sample.extansions.openScreen
import me.dmdev.rxpm.sample.ui.auth.phone.AuthByPhoneFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.openScreen(AuthByPhoneFragment(), addToBackStack = false)
        }
    }
}

