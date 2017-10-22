package me.dmdev.rxpm.widget

import com.nhaarman.mockito_kotlin.spy
import io.reactivex.observers.TestObserver
import me.dmdev.rxpm.PresentationModel
import org.junit.Test

class InputControlTest {

    @Test
    fun testFilterIfValueNotChanged() {

        val pm = spy<PresentationModel>()
        val input = pm.inputControl()

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

        val pm = spy<PresentationModel>()
        val input = pm.inputControl(
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