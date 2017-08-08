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

}