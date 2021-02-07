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

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.widget.InputControl
import me.dmdev.rxpm.widget.inputControl
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue


class InputValidatorTest {

    private val initialText = ""
    private val errorText = "error"

    private lateinit var pm: PresentationModel
    private lateinit var inputControl: InputControl
    private lateinit var inputValidator: InputValidator

    @Before fun init() {
        pm = spy()
        inputControl = pm.inputControl(initialText)
        inputValidator = InputValidator(
            inputControl = inputControl,
            required = true,
            validateOnFocusLoss = false
        )
    }

    @Test fun inputIsValid() {

        inputValidator.addValidation({ _: String -> true } to errorText)

        assertTrue(inputValidator.validate())
        assertNull(inputControl.error.valueOrNull)

    }

    @Test fun inputIsInvalid() {

        inputValidator.addValidation({ _: String -> false } to errorText)

        assertFalse(inputValidator.validate())
        assertEquals(errorText, inputControl.error.valueOrNull)

    }

    @Test fun callingMultipleValidations() {

        val validation1 = mock<(String) -> Boolean> {
            onGeneric { invoke(initialText) }.doReturn(true)
        }

        val validation2 = mock<(String) -> Boolean> {
            onGeneric { invoke(initialText) }.doReturn(true)
        }

        val validation3 = mock<(String) -> Boolean> {
            onGeneric { invoke(initialText) }.doReturn(true)
        }

        inputValidator.addValidation(validation1 to errorText)
        inputValidator.addValidation(validation2 to errorText)
        inputValidator.addValidation(validation3 to errorText)

        assertTrue(inputValidator.validate())
        assertNull(inputControl.error.valueOrNull)

        verify(validation1).invoke(initialText)
        verify(validation2).invoke(initialText)
        verify(validation3).invoke(initialText)

    }

    @Test fun skipOptionalFieldValidationIfIsEmpty() {

        val inputValidator = InputValidator(
            inputControl = inputControl,
            required = false,
            validateOnFocusLoss = false
        )

        inputValidator.addValidation({ _: String -> false } to errorText)

        assertTrue(inputValidator.validate())
        assertNull(inputControl.error.valueOrNull)
    }

    @Test fun emptyInputValid() {

        inputValidator.empty(errorText)

        inputControl.text.relay.accept("abc")

        assertTrue(inputValidator.validate())
        assertNull(inputControl.error.valueOrNull)

    }

    @Test fun emptyInputInvalid() {

        inputValidator.empty(errorText)

        inputControl.text.relay.accept("")

        assertFalse(inputValidator.validate())
        assertEquals(errorText, inputControl.error.valueOrNull)

    }

    @Test fun patternInputValid() {

        inputValidator.pattern("\\d+", errorText)

        inputControl.text.relay.accept("123")

        assertTrue(inputValidator.validate())
        assertNull(inputControl.error.valueOrNull)

    }

    @Test fun patternInputInvalid() {

        inputValidator.pattern("\\d+", errorText)

        inputControl.text.relay.accept("abc")

        assertFalse(inputValidator.validate())
        assertEquals(errorText, inputControl.error.valueOrNull)

    }

    @Test fun minSymbolsInputValid() {

        inputValidator.minSymbols(3, errorText)

        inputControl.text.relay.accept("123")

        assertTrue(inputValidator.validate())
        assertNull(inputControl.error.valueOrNull)

    }

    @Test fun minSymbolsInvalid() {

        inputValidator.minSymbols(3, errorText)

        inputControl.text.relay.accept("12")

        assertFalse(inputValidator.validate())
        assertEquals(errorText, inputControl.error.valueOrNull)

    }

    @Test fun twoInputEquals() {

        val inputControl2 = pm.inputControl()

        inputValidator.equalsTo(inputControl2, errorText)

        inputControl.text.relay.accept("123")
        inputControl2.text.relay.accept("123")

        assertTrue(inputValidator.validate())
        assertNull(inputControl.error.valueOrNull)

    }

    @Test fun twoInputNotEquals() {

        val inputControl2 = pm.inputControl()

        inputValidator.equalsTo(inputControl2, errorText)

        inputControl.text.relay.accept("123")
        inputControl2.text.relay.accept("321")

        assertFalse(inputValidator.validate())
        assertEquals(errorText, inputControl.error.valueOrNull)

    }

    @Test fun customCheckValid() {

        inputValidator.valid({ true }, errorText)

        assertTrue(inputValidator.validate())
        assertNull(inputControl.error.valueOrNull)

    }

    @Test fun customCheckInvalid() {

        inputValidator.valid({ false }, errorText)

        assertFalse(inputValidator.validate())
        assertEquals(errorText, inputControl.error.valueOrNull)

    }

}