package me.dmdev.rxpm.validation

import me.dmdev.rxpm.*
import me.dmdev.rxpm.widget.*

class FormValidator internal constructor() {

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
}

@Suppress("unused")
fun PresentationModel.formValidator(init: FormValidator.() -> Unit): FormValidator {
    val formValidator = FormValidator()
    formValidator.init()
    return formValidator
}

fun FormValidator.input(
    inputControl: InputControl,
    required: Boolean = true,
    init: InputValidator.() -> Unit
) {
    val inputValidator = InputValidator(inputControl, required)
    inputValidator.init()
    inputValidators.add(inputValidator)
}
