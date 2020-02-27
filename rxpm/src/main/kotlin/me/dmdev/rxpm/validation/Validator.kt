package me.dmdev.rxpm.validation


/**
 * Interface used to define whether a condition is satisfied.
 */
interface Validator {

    /**
     * Runs condition check.
     *
     * @return true if condition is valid, false otherwise.
     */
    fun validate(): Boolean
}