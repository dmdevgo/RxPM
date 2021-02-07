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
import me.dmdev.rxpm.test.PmTestHelper
import me.dmdev.rxpm.widget.inputControl
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FormValidatorTest {

    private val errorText = "error"
    private lateinit var pm : PresentationModel
    private lateinit var pmTestHelper: PmTestHelper

    @Before fun init() {
        pm = spy()
        pmTestHelper = PmTestHelper(pm)
    }

    @Test fun formIsValid() {

        val formValidator = pm.formValidator {}

        val validator1 = mock<Validator> {
            onGeneric { validate() }.doReturn(true)
        }

        val validator2 = mock<Validator> {
            onGeneric { validate() }.doReturn(true)
        }

        val validator3 = mock<Validator> {
            onGeneric { validate() }.doReturn(true)
        }

        formValidator.addValidator(validator1)
        formValidator.addValidator(validator2)
        formValidator.addValidator(validator3)

        assertTrue(formValidator.validate())

        verify(validator1).validate()
        verify(validator2).validate()
        verify(validator3).validate()

    }

    @Test fun formIsInvalid() {

        val formValidator = pm.formValidator {}

        val validator1 = mock<Validator> {
            onGeneric { validate() }.doReturn(true)
        }

        val validator2 = mock<Validator> {
            onGeneric { validate() }.doReturn(false)
        }

        val validator3 = mock<Validator> {
            onGeneric { validate() }.doReturn(true)
        }

        formValidator.addValidator(validator1)
        formValidator.addValidator(validator2)
        formValidator.addValidator(validator3)

        assertFalse(formValidator.validate())

        verify(validator1).validate()
        verify(validator2).validate()
        verify(validator3).validate()

    }

    @Test fun validateInputOnFocusLoss() {

        val inputControl = pm.inputControl("abc")

        val formValidator = pm.formValidator {

            input(
                inputControl = inputControl,
                required = true,
                validateOnFocusLoss = true
            ) {
                empty(errorText)
            }
        }

        pmTestHelper.setLifecycleTo(PresentationModel.Lifecycle.RESUMED)
        inputControl.focusChanges.consumer.accept(true)
        inputControl.text.relay.accept("")

        assertNull(inputControl.error.valueOrNull)

        inputControl.focusChanges.consumer.accept(false)

        assertEquals(errorText, inputControl.error.valueOrNull)

    }
}