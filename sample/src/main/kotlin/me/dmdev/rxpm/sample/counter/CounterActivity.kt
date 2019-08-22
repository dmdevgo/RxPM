package me.dmdev.rxpm.sample.counter

import android.os.*
import com.jakewharton.rxbinding3.view.*
import kotlinx.android.synthetic.main.activity_counter.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.base.*
import me.dmdev.rxpm.sample.R

class CounterActivity : PmActivity<CounterPm>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counter)
    }

    override fun providePresentationModel() = CounterPm()

    override fun onBindPresentationModel(pm: CounterPm) {

        pm.count bindTo { counterText.text = it.toString() }
        pm.minusButtonEnabled bindTo minusButton::setEnabled
        pm.plusButtonEnabled bindTo plusButton::setEnabled

        minusButton.clicks() bindTo pm.minusButtonClicks
        plusButton.clicks() bindTo pm.plusButtonClicks
    }
}