package me.dmdev.rxpm.widget

import me.dmdev.rxpm.*

typealias ValidateFunction = (input: String) -> Boolean

class Validator(
    val validateFunction: ValidateFunction,
    val errorText: String
)

class InputValidator(
    private val inputControl: InputControl
): PresentationModel() {
    val validators = mutableListOf<Validator>()

    fun validate(): Boolean {
        validators.forEach {
            if (!it.validateFunction(inputControl.text.value)){
                inputControl.error.consumer.accept(it.errorText)
                return false
            }
        }
        return true
    }
}

class ValidatorControl : PresentationModel() {
    val fields = mutableListOf<InputValidator>()

    fun validate() : Boolean {
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
    validators.add(Validator(String::isNotEmpty, message))
}

fun InputValidator.pattern(regex: String, message: String) {
    validators.add(Validator({ regex.toRegex().matches(it) }, message))
}

fun InputValidator.invalid(validator: ValidateFunction, message: String) {
    validators.add(Validator(validator, message))
}

fun InputValidator.minSymbols(number: Int, message: String) {
    validators.add(Validator({ it.length >= number }, message))
}

fun InputValidator.confirm(input: InputControl, message: String) {
    validators.add(Validator( { it == input.text.valueOrNull } , message))
}