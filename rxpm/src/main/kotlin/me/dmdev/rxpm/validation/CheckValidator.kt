package me.dmdev.rxpm.validation

class CheckValidator internal constructor(
    private val validation: () -> Boolean,
    private val doOnFalse: () -> Unit
) : Validator {

    override fun validate(): Boolean {
        val isValid = validation()

        if (!isValid) {
            doOnFalse()
        }

        return isValid
    }
}