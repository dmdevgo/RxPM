package me.dmdev.rxpm.widget

import me.dmdev.rxpm.*

class InputValidator(
    private val inputControl: InputControl
) : PresentationModel() {

    val validators = mutableListOf<Pair<(input: String) -> Boolean, String>>()

    fun validate(): Boolean {
        validators.forEach {
            if (!it.first(inputControl.text.value)) {
                inputControl.error.consumer.accept(it.second)
                return false
            }
        }
        return true
    }
}

class ValidatorControl : PresentationModel() {

    val fields = mutableListOf<InputValidator>()

    fun validate(): Boolean {
        var isValid = true
        fields.forEach {
            if (!it.validate()) {
                isValid = false
            }
        }
        return isValid
    }
}

fun validator(block: ValidatorControl.() -> Unit): ValidatorControl {
    val from = ValidatorControl()
    from.block()
    return from
}

fun ValidatorControl.input(inputControl: InputControl, block: InputValidator.() -> Unit) {
    val fieldValidator = InputValidator(inputControl)
    fieldValidator.block()
    fields.add(fieldValidator)
}

fun InputValidator.empty(message: String) {
    validators.add(String::isNotEmpty to message)
}

fun InputValidator.pattern(regex: String, message: String) {
    validators.add(
        { str: String -> regex.toRegex().matches(str) } to message
    )
}

fun InputValidator.invalid(validator: (input: String) -> Boolean, message: String) {
    validators.add(validator to message)
}

fun InputValidator.minSymbols(number: Int, message: String) {
    validators.add({ str: String -> str.length >= number } to message)
}

fun InputValidator.confirm(input: InputControl, message: String) {
    validators.add({ str: String -> str == input.text.valueOrNull } to message)
}