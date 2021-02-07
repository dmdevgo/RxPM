/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2021 Dmitriy Gorbunov (dmitriy.goto@gmail.com)
 *                     and Vasili Chyrvon (vasili.chyrvon@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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