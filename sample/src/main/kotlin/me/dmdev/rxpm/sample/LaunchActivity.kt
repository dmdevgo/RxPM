package me.dmdev.rxpm.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_launch.*
import me.dmdev.rxpm.sample.counter.CounterActivity
import me.dmdev.rxpm.sample.main.MainActivity
import me.dmdev.rxpm.sample.permissions.PermissionsActivity

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        counterSample.setOnClickListener {
            launchActivity(CounterActivity::class.java)
        }

        mainSample.setOnClickListener {
            launchActivity(MainActivity::class.java)
        }

        permissionsSample.setOnClickListener {
            launchActivity(PermissionsActivity::class.java)
        }
    }

    private fun launchActivity(clazz: Class<out Activity>) {
        startActivity(Intent(this, clazz))
    }
}