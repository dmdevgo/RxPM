package me.dmdev.rxpm.validation

import me.dmdev.rxpm.widget.*

class InputValidator internal constructor(
    internal val inputControl: InputControl,
    private val required: Boolean,
    internal val validateOnFocusLoss: Boolean
) : Validator {

    private val validations = mutableListOf<Pair<(String) -> Boolean, String>>()

    fun addValidation(validation: Pair<(String) -> Boolean, String>) {
        validations.add(validation)
    }

    override fun validate(): Boolean {

        if (inputControl.text.value.isBlank() && !required) {
            return true
        }

        validations.forEach { (predicate, errorMessage) ->
            if (!predicate(inputControl.text.value)) {
                inputControl.error.relay.accept(errorMessage)
                return false
            }
        }

        return true
    }
}

fun InputValidator.empty(errorMessage: String) {
    addValidation(String::isNotEmpty to errorMessage)
}

fun InputValidator.pattern(regex: String, errorMessage: String) {
    addValidation(
        { str: String -> regex.toRegex().matches(str) } to errorMessage
    )
}

fun InputValidator.valid(validation: (input: String) -> Boolean, errorMessage: String) {
    addValidation(validation to errorMessage)
}

fun InputValidator.minSymbols(number: Int, errorMessage: String) {
    addValidation({ str: String -> str.length >= number } to errorMessage)
}

fun InputValidator.equalsTo(input: InputControl, errorMessage: String) {
    addValidation({ str: String -> str == input.text.valueOrNull } to errorMessage)
}