package me.dmdev.rxpm.sample.validation

import android.os.*
import android.widget.*
import com.jakewharton.rxbinding3.view.*
import kotlinx.android.synthetic.main.activity_form.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.base.*
import me.dmdev.rxpm.sample.*
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.widget.*

class FormValidationActivity : PmActivity<FormValidationPm>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
    }

    override fun providePresentationModel(): FormValidationPm {
        return FormValidationPm(App.component.phoneUtil)
    }

    override fun onBindPresentationModel(pm: FormValidationPm) {
        pm.name bindTo nameEditLayout
        pm.email bindTo emailEditLayout
        pm.phone bindTo phoneEditLayout
        pm.password bindTo passwordEditLayout
        pm.confirmPassword bindTo confirmPasswordEditLayout
        pm.termsCheckBox bindTo termsCheckbox

        pm.acceptTermsOfUse bindTo {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        validateButton.clicks() bindTo pm.validateButtonClicks
    }
}