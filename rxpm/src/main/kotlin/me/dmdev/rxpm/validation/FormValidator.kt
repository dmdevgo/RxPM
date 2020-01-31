package me.dmdev.rxpm.validation

import me.dmdev.rxpm.*
import me.dmdev.rxpm.widget.*

class FormValidator internal constructor(): PresentationModel(), Validator {

    private val validators = mutableListOf<Validator>()

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
    addValidator(inputValidator)
}

fun FormValidator.check(
    state: State<Boolean>,
    doOnFalse: () -> Unit = {}
) {
    addValidator(
        CheckValidator(
            validation = { state.valueOrNull == true },
            doOnFalse = doOnFalse
        )
    )
}

fun FormValidator.check(
    checkControl: CheckControl,
    doOnFalse: () -> Unit = {}
) {
    check(checkControl.checked, doOnFalse)
}

fun FormValidator.check(
    validation: () -> Boolean,
    doOnFalse: () -> Unit = {}
) {
    addValidator(
        CheckValidator(
            validation = validation,
            doOnFalse = doOnFalse
        )
    )
}