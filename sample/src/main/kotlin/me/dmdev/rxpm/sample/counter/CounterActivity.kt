package me.dmdev.rxpm.sample.counter

import android.os.Bundle
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.text
import kotlinx.android.synthetic.main.activity_counter.*
import me.dmdev.rxpm.base.PmSupportActivity
import me.dmdev.rxpm.sample.R

class CounterActivity : PmSupportActivity<CounterPm>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counter)
    }

    override fun providePresentationModel() = CounterPm()

    override fun onBindPresentationModel(pm: CounterPm) {

        pm.count.observable.map { it.toString() } bindTo counterText.text()
        pm.minusButtonEnubled bindTo minusButton::setEnabled
        pm.plusButtonEnubled bindTo plusButton::setEnabled

        minusButton.clicks() bindTo pm.minusButtonClicks
        plusButton.clicks() bindTo pm.plusButtonClicks
    }
}