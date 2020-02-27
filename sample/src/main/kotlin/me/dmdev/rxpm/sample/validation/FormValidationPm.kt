package me.dmdev.rxpm.sample.validation

import me.dmdev.rxpm.*
import me.dmdev.rxpm.sample.main.util.*
import me.dmdev.rxpm.validation.*
import me.dmdev.rxpm.widget.*

class FormValidationPm(
    private val phoneUtil: PhoneUtil
) : PresentationModel() {

    val name = inputControl(
        formatter = { it.replace("[^a-zA-Z ]".toRegex(), "").take(100) }
    )
    val email = inputControl()
    val phone = inputControl(
        initialText = "+7",
        formatter = { phoneUtil.formatPhoneNumber(it) }
    )
    val password = inputControl()
    val confirmPassword = inputControl()

    val termsCheckBox = checkControl(false)

    val acceptTermsOfUse = command<String>()

    val validateButtonClicks = action<Unit> {
        doOnNext { formValidator.validate() }
    }

    private val formValidator = formValidator {

        input(name) {
            empty("Input Name")
        }

        input(email, required = false) {
            pattern(ANDROID_EMAIL_PATTERN, "Invalid e-mail address")
        }

        input(phone, validateOnFocusLoss = true) {
            valid(phoneUtil::isValidPhone, "Invalid phone number")
        }

        input(password) {
            empty("Input Password")
            minSymbols(6, "Minimum 6 symbols")
            pattern(
                regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d]).{6,}\$",
                errorMessage = "The password must contain a large and small letters, numbers."
            )
        }

        input(confirmPassword) {
            empty("Confirm Password")
            equalsTo(password, "Passwords do not match")
        }

        check(termsCheckBox) {
            acceptTermsOfUse.accept("Please accept the terms of use")
        }
    }
}

private const val ANDROID_EMAIL_PATTERN = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
        "\\@" +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
        "(" +
        "\\." +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
        ")+"