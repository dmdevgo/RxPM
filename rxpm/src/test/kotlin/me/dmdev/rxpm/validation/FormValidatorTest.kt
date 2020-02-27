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