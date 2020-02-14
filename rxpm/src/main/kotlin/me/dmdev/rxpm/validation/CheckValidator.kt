package me.dmdev.rxpm.validation


/**
 * Implements [Validator] that uses a predefined [check condition][validation].
 * If the condition is invalid, then [doOnInvalid] is called.
 *
 * @see InputValidator
 * @see FormValidator
 */
class CheckValidator internal constructor(
    private val validation: () -> Boolean,
    private val doOnInvalid: () -> Unit
) : Validator {

    override fun validate(): Boolean {
        val isValid = validation()

        if (!isValid) {
            doOnInvalid()
        }

        return isValid
    }
}