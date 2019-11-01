package me.dmdev.rxpm.sample

import android.app.*
import android.content.*
import android.os.*
import androidx.appcompat.app.*
import kotlinx.android.synthetic.main.activity_launch.*
import me.dmdev.rxpm.sample.counter.*
import me.dmdev.rxpm.sample.main.*
import me.dmdev.rxpm.sample.validation.*

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

        formValidationSample.setOnClickListener {
            launchActivity(FormValidationActivity::class.java)
        }
    }

    private fun launchActivity(clazz: Class<out Activity>) {
        startActivity(Intent(this, clazz))
    }
}