package me.dmdev.rxpm.widget

import me.dmdev.rxpm.PresentationModel
import org.junit.Before
import org.junit.Test

class InputControlTest {

    private lateinit var presentationModel: PresentationModel

    @Before fun setUp() {
        presentationModel = object : PresentationModel() {}
    }

    @Test fun filterDuplicateChanges() {
        val inputControl = presentationModel.inputControl()
        val testObserver = inputControl.text.observable.test()

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