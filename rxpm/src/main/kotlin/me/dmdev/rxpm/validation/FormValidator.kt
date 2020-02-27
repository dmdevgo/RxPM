package me.dmdev.rxpm.validation

import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.widget.CheckControl
import me.dmdev.rxpm.widget.InputControl


/**
 * Use this class to validate the form.
 * To check the [input fields][input] and [checkbox][check] [create][formValidator] FormValidator using DSL.
 * Also you can create your own validators by analogy and use them together.
 *
 * @see InputValidator
 * @see CheckValidator
 */
class FormValidator internal constructor(): PresentationModel(), Validator {

    private val validators = mutableListOf<Validator>()

    /**
     * Adds a [validator].
     */
    fun addValidator(validator: Validator) {
        validators.add(validator)
    }

    override fun validate(): Boolean {
        var isFormValid = true
        validators.forEach { validator ->
            val isValid = validator.validate()

            if (!isValid) {
                isFormValid = false
            }
        }
        return isFormValid
    }

    override fun onCreate() {

        validators.forEach { validator ->
            if (validator is InputValidator && validator.validateOnFocusLoss) {
                validator.inputControl.focus.observable
                    .skip(1)
                    .filter { hasFocus -> !hasFocus }
                    .subscribe {
                        validator.validate()
                    }
                    .untilDestroy()
            }
        }
    }
}

/**
 * Creates the [FormValidator]. Add [input][input] and [check][check] validators in [init].
 */
@Suppress("unused")
fun PresentationModel.formValidator(init: FormValidator.() -> Unit): FormValidator {
    val formValidator = FormValidator()
    formValidator.init()
    return formValidator.apply {
        attachToParent(this@formValidator)
    }
}


/**
 * Creates the [InputValidator] for [inputControl] and adds it to the [FormValidator].
 */
fun FormValidator.input(
    inputControl: InputControl,
    required: Boolean = true,
    validateOnFocusLoss: Boolean = false,
    init: InputValidator.() -> Unit
) {
    val inputValidator = InputValidator(inputControl, required, validateOnFocusLoss)
    inputValidator.init()
    addValidator(inputValidator)
}


/**
 * Creates the [CheckValidator] for [checkControl] and adds it to the [FormValidator].
 */
fun FormValidator.check(
    checkControl: CheckControl,
    doOnInvalid: () -> Unit = {}
) {
    val checkValidator = CheckValidator(
        validation = { checkControl.checked.valueOrNull == true },
        doOnInvalid = doOnInvalid
    )
    addValidator(checkValidator)
}