package me.dmdev.rxpm.widget

import io.reactivex.observers.TestObserver
import org.junit.Test

/**
 * @author Dmitriy Gorbunov
 */
class CheckControlTest {

    @Test
    fun testFilterIfValueNotChanged() {

        val checkbox = CheckControl()

        val to = TestObserver<Boolean>()
        checkbox.checked.observable.subscribe(to)

        checkbox.checkedChanges.consumer.accept(true)
        checkbox.checkedChanges.consumer.accept(true)
        checkbox.checkedChanges.consumer.accept(false)
        checkbox.checkedChanges.consumer.accept(false)
        checkbox.checkedChanges.consumer.accept(true)
        checkbox.checkedChanges.consumer.accept(false)

        to.assertValues(false, //initial value
                        true, false, true, false)
        to.assertNoErrors()
    }

}