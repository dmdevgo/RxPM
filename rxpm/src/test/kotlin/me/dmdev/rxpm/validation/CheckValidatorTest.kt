package me.dmdev.rxpm.validation

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class CheckValidatorTest {

    @Test fun checkIsValid() {

        val doOnInvalid = mock<() -> Unit>()

        val checkValidator = CheckValidator(
            validation = { true },
            doOnInvalid = doOnInvalid
        )

        assertTrue(checkValidator.validate())
        verifyZeroInteractions(doOnInvalid)
    }

    @Test fun checkIsInvalid() {

        val doOnInvalid = mock<() -> Unit>()

        val checkValidator = CheckValidator(
            validation = { false },
            doOnInvalid = doOnInvalid
        )

        assertFalse(checkValidator.validate())
        verify(doOnInvalid).invoke()
    }
}