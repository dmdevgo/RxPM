package me.dmdev.rxpm.sample.validation

import me.dmdev.rxpm.*
import me.dmdev.rxpm.widget.*

class FormValidationPm : PresentationModel() {

    val name = inputControl()
    val email = inputControl()

    val validateButtonClicks = action<Unit> {
        doOnNext { formValidator.validate() }
    }

    private val formValidator = validator {

        input(name) {
            empty("Input Name")
        }

        input(email) {
            empty("Input E-mail")
            pattern(ANDROID_EMAIL_PATTERN, "Invalid e-mail address")
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