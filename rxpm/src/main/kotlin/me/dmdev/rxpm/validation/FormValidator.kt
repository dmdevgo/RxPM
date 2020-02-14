package me.dmdev.rxpm.validation

import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.widget.CheckControl
import me.dmdev.rxpm.widget.InputControl

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
    checkControl: CheckControl,
    doOnInvalid: () -> Unit = {}
) {
    val checkValidator = CheckValidator(
        validation = { checkControl.checked.valueOrNull == true },
        doOnInvalid = doOnInvalid
    )
    addValidator(checkValidator)
}