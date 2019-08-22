package me.dmdev.rxpm.widget

import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.Lifecycle.CREATED
import me.dmdev.rxpm.test.PmTestHelper
import org.junit.Test

class CheckControlTest {

    @Test fun filterIfValueNotChanged() {
        val pm = object : PresentationModel() {}
        val pmTestHelper = PmTestHelper(pm)

        val checkbox = pm.checkControl()

        pmTestHelper.setLifecycleTo(CREATED)

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