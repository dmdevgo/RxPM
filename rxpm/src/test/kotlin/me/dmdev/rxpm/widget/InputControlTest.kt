package me.dmdev.rxpm.widget

import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.Lifecycle.CREATED
import me.dmdev.rxpm.test.PmTestHelper
import org.junit.Before
import org.junit.Test

class InputControlTest {

    private lateinit var presentationModel: PresentationModel
    private lateinit var pmTestHelper: PmTestHelper

    @Before fun setUp() {
        presentationModel = object : PresentationModel() {}
        pmTestHelper = PmTestHelper(presentationModel)
    }

    @Test fun filterDuplicateChanges() {
        val inputControl = presentationModel.inputControl()
        val testObserver = inputControl.text.observable.test()

        pmTestHelper.setLifecycleTo(CREATED)
        inputControl.textChanges.consumer.run {
            accept("a")
            accept("a")
            accept("ab")
            accept("ab")
            accept("abc")
            accept("abc")
        }

        testObserver
            .assertValues(
                "", // initial value
                "a",
                "ab",
                "abc"
            )
            .assertNoErrors()
    }

    @Test fun formatInput() {
        val inputControl = presentationModel.inputControl(
            formatter = { it.toUpperCase() }
        )
        val testObserver = inputControl.text.observable.test()

        pmTestHelper.setLifecycleTo(CREATED)

        inputControl.textChanges.consumer.run {
            accept("a")
            accept("ab")
            accept("abc")
        }

        testObserver
            .assertValues(
                "", // initial value
                "A",
                "AB",
                "ABC"
            )
            .assertNoErrors()
    }

}