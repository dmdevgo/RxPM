package me.dmdev.rxpm.sample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_launch.*
import me.dmdev.rxpm.sample.main.AppActivity

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        mainSample.setOnClickListener {
            startActivity(Intent(this, AppActivity::class.java))
        }
    }
}