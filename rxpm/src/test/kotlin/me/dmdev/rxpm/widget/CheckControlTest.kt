package me.dmdev.rxpm.widget

import com.nhaarman.mockito_kotlin.spy
import io.reactivex.observers.TestObserver
import me.dmdev.rxpm.PresentationModel
import org.junit.Test

/**
 * @author Dmitriy Gorbunov
 */
class CheckControlTest {

    @Test
    fun testFilterIfValueNotChanged() {

        val pm = spy<PresentationModel>()
        val checkbox = pm.checkControl()

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