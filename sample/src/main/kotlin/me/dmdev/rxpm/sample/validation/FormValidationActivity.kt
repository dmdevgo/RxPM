package me.dmdev.rxpm.sample.validation

import android.os.*
import com.jakewharton.rxbinding3.view.*
import kotlinx.android.synthetic.main.activity_form.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.base.*
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.widget.*

class FormValidationActivity : PmActivity<FormValidationPm>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
    }

    override fun providePresentationModel() = FormValidationPm()

    override fun onBindPresentationModel(pm: FormValidationPm) {
        pm.name bindTo nameEditLayout
        pm.email bindTo emailEditLayout
        validateButton.clicks() bindTo pm.validateButtonClicks
    }
}