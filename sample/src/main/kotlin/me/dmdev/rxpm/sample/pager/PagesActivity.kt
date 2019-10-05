package me.dmdev.rxpm.sample.pager

import android.os.*
import androidx.appcompat.app.*
import kotlinx.android.synthetic.main.activity_pages.*
import me.dmdev.rxpm.sample.*

class PagesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pages)

        viewPager.adapter = PagesAdapter(supportFragmentManager)
        viewPager.offscreenPageLimit = 1
    }
}