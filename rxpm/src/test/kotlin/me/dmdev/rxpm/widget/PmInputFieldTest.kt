package me.dmdev.rxpm.widget

import io.reactivex.observers.TestObserver
import org.junit.Test

/**
 * @author Dmitriy Gorbunov
 */
class PmInputFieldTest {

    @Test
    fun testFilterIfValueNotChanged() {

        val input = PmInputField()

        val to = TestObserver<String>()
        input.text.subscribe(to)

        input.textChangesConsumer.accept("a")
        input.textChangesConsumer.accept("a")
        input.textChangesConsumer.accept("ab")
        input.textChangesConsumer.accept("ab")
        input.textChangesConsumer.accept("abc")
        input.textChangesConsumer.accept("abc")

        to.assertValues("", "a", "ab", "abc")
        to.assertNoErrors()
    }

    @Test
    fun testMapper() {

        val input = PmInputField(
                formatter = { it.toUpperCase() }
        )

        val to = TestObserver<String>()
        input.text.subscribe(to)

        input.textChangesConsumer.accept("a")
        input.textChangesConsumer.accept("ab")
        input.textChangesConsumer.accept("abc")

        to.assertValues("", "A", "AB", "ABC")
        to.assertNoErrors()
    }

    @Test
    fun testValidator() {

        val IS_EMPTY_ERROR = "Is empty"
        val input = PmInputField(
                validator = {
                    if (it.isNotEmpty()) "" //is valid
                    else IS_EMPTY_ERROR // error message
                }
        )

        val to = TestObserver<String>()
        input.error.subscribe(to)

        input.textChangesConsumer.accept("")
        input.validate()
        input.textChangesConsumer.accept("a")
        input.validate()

        to.assertValues(IS_EMPTY_ERROR,
                        "", // Clear the error if the user entered text
                        "") // Is valid text
        to.assertNoErrors()
    }

}