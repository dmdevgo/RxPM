package me.dmdev.rxpm.widget

import me.dmdev.rxpm.PresentationModel
import org.junit.Test

class CheckControlTest {

    @Test fun filterIfValueNotChanged() {
        val pm = object : PresentationModel() {}
        val checkbox = pm.checkControl()

        val testObserver = checkbox.checked.observable.test()

        checkbox.checkedChanges.consumer.run {
            accept(true)
            accept(true)
            accept(false)
            accept(false)
            accept(true)
            accept(false)
        }

        testObserver
            .assertValues(
                false, // initial value
                true,
                false,
                true,
                false
            )
            .assertNoErrors()
    }
}