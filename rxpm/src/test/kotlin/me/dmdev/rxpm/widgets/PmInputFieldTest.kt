package me.dmdev.rxpm.widgets

import io.reactivex.observers.TestObserver
import org.junit.Test
import ru.mobileup.yami.pm.PmInputField

/**
 * @author Dmitriy Gorbunov
 */
class PmInputFieldTest {

    @Test
    fun testFilterIfValueNotChanged() {

        val input = PmInputField()

        val to = TestObserver<String>()
        input.text.subscribe(to)

        input.textChangeConsumer.accept("a")
        input.textChangeConsumer.accept("a")
        input.textChangeConsumer.accept("ab")
        input.textChangeConsumer.accept("ab")
        input.textChangeConsumer.accept("abc")
        input.textChangeConsumer.accept("abc")

        to.assertValues("", "a", "ab", "abc")
        to.assertNoErrors()
    }

    @Test
    fun testMapper() {

        val input = PmInputField().apply {
            mapper = { it.toUpperCase() }
        }

        val to = TestObserver<String>()
        input.text.subscribe(to)

        input.textChangeConsumer.accept("a")
        input.textChangeConsumer.accept("ab")
        input.textChangeConsumer.accept("abc")

        to.assertValues("", "A", "AB", "ABC")
        to.assertNoErrors()
    }

    @Test
    fun testValidator() {

        val IS_EMPTY_ERROR = "Is empty"
        val input = PmInputField().apply {
            validator = {
                if (it.isNotEmpty()) "" //is valid
                else IS_EMPTY_ERROR // error message
            }
        }

        val to = TestObserver<String>()
        input.error.subscribe(to)

        input.textChangeConsumer.accept("")
        input.validate()
        input.textChangeConsumer.accept("a")
        input.validate()

        to.assertValues(IS_EMPTY_ERROR,
                        "", // Clear the error if the user entered text
                        "") // Is valid text
        to.assertNoErrors()
    }

}