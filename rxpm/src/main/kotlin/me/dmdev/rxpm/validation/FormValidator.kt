package me.dmdev.rxpm.validation

import me.dmdev.rxpm.*
import me.dmdev.rxpm.widget.*

class FormValidator internal constructor(): PresentationModel() {

    internal val inputValidators = mutableListOf<InputValidator>()

    fun validate(): Boolean {
        var isFormValid = true
        inputValidators.forEach { inputValidator ->
            val isFieldValid = inputValidator.validate()

            if (!isFieldValid) {
                isFormValid = false
            }
        }
        return isFormValid
    }

    override fun onCreate() {

        inputValidators.forEach { validator ->
            if (validator.validateOnFocusLoss) {
                validator.inputControl.focus.observable
                    .skip(1)
                    .filter { focus -> focus.not() }
                    .subscribe {
                        validator.validate()
                    }
                    .untilDestroy()
            }
        }
    }
}

@Suppress("unused")
fun PresentationModel.formValidator(init: FormValidator.() -> Unit): FormValidator {
    val formValidator = FormValidator()
    formValidator.init()
    return formValidator.apply {
        attachToParent(this@formValidator)
    }
}

fun FormValidator.input(
    inputControl: InputControl,
    required: Boolean = true,
    validateOnFocusLoss: Boolean = false,
    init: InputValidator.() -> Unit
) {
    val inputValidator = InputValidator(inputControl, required, validateOnFocusLoss)
    inputValidator.init()
    inputValidators.add(inputValidator)
}
