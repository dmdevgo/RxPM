package me.dmdev.rxpm.validation

import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.widget.InputControl


/**
 * Validates a text from [InputControl] and post an error text if it is invalid.
 * You can [add][addValidation] multiple validations. This class is not used directly,
 * create a form validator using [DSL][PresentationModel.formValidator] instead,
 * and add into it input checks such as [empty], [pattern] and other.
 *
 * @see CheckValidator
 * @see FormValidator
 */
class InputValidator internal constructor(
    internal val inputControl: InputControl,
    private val required: Boolean,
    internal val validateOnFocusLoss: Boolean
) : Validator {

    private val validations = mutableListOf<Pair<(String) -> Boolean, String>>()

    /**
     * Adds a text validation.
     * @param validation - pair of validation and error text for [InputControl].
     */
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

/**
 * Adds a check that the text is not empty.
 */
fun InputValidator.empty(errorMessage: String) {
    addValidation(String::isNotEmpty to errorMessage)
}

/**
 * Adds a validation based on a [regular expression][regex].
 */
fun InputValidator.pattern(regex: String, errorMessage: String) {
    addValidation(
        { str: String -> regex.toRegex().matches(str) } to errorMessage
    )
}

/**
 * Adds a custom condition to check the text.
 */
fun InputValidator.valid(validation: (input: String) -> Boolean, errorMessage: String) {
    addValidation(validation to errorMessage)
}

/**
 * Adds a check for a minimum [number] of symbols.
 */
fun InputValidator.minSymbols(number: Int, errorMessage: String) {
    addValidation({ str: String -> str.length >= number } to errorMessage)
}

/**
 * Adds a check that the text from another [input] is the same.
 * Used for example to confirm password entry.
 */
fun InputValidator.equalsTo(input: InputControl, errorMessage: String) {
    addValidation({ str: String -> str == input.text.valueOrNull } to errorMessage)
}