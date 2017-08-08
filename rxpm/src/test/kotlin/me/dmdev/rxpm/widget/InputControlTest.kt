package me.dmdev.rxpm.widget

import io.reactivex.observers.TestObserver
import org.junit.Test

/**
 * @author Dmitriy Gorbunov
 */
class InputControlTest {

    @Test
    fun testFilterIfValueNotChanged() {

        val input = InputControl()

        val to = TestObserver<String>()
        input.text.observable.subscribe(to)

        input.textChanges.consumer.accept("a")
        input.textChanges.consumer.accept("a")
        input.textChanges.consumer.accept("ab")
        input.textChanges.consumer.accept("ab")
        input.textChanges.consumer.accept("abc")
        input.textChanges.consumer.accept("abc")

        to.assertValues("", "a", "ab", "abc")
        to.assertNoErrors()
    }

    @Test
    fun testFormatter() {

        val input = InputControl(
                formatter = { it.toUpperCase() }
        )

        val to = TestObserver<String>()
        input.text.observable.subscribe(to)

        input.textChanges.consumer.accept("a")
        input.textChanges.consumer.accept("ab")
        input.textChanges.consumer.accept("abc")

        to.assertValues("", "A", "AB", "ABC")
        to.assertNoErrors()
    }

    @Test
    fun testValidator() {

        val IS_EMPTY_ERROR = "Is empty"
        val input = InputControl(
                validator = {
                    if (it.isNotEmpty()) "" //is valid
                    else IS_EMPTY_ERROR // error message
                }
        )

        val to = TestObserver<String>()
        input.error.observable.subscribe(to)

        input.textChanges.consumer.accept("")
        input.validate()
        input.textChanges.consumer.accept("a")
        input.validate()

        to.assertValues(IS_EMPTY_ERROR,
                        "", // Clear the error if the user entered text
                        "") // Is valid text
        to.assertNoErrors()
    }

}