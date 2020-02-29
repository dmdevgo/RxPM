package me.dmdev.rxpm.widget

import me.dmdev.rxpm.*
import me.dmdev.rxpm.PresentationModel.Lifecycle.*
import me.dmdev.rxpm.test.*
import org.junit.*

class InputControlTest {

    private lateinit var presentationModel: PresentationModel
    private lateinit var pmTestHelper: PmTestHelper

    @Before
    fun setUp() {
        presentationModel = object : PresentationModel() {}
        pmTestHelper = PmTestHelper(presentationModel)
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

    @Test fun notFilterDuplicateValues() {

        val inputControl = presentationModel.inputControl(
            formatter = { it.take(3) }
        )

        val testObserver = inputControl.text.observable.test()

        pmTestHelper.setLifecycleTo(CREATED)

        inputControl.textChanges.consumer.run {
            accept("a")
            accept("ab")
            accept("abc")
            accept("abcd")
        }

        testObserver
            .assertValues(
                "", // initial value
                "a",
                "ab",
                "abc",
                "abc" // clear user input after formatting because editText contains "abcd"
            )
            .assertNoErrors()
    }

    @Test fun filterIfFocusNotChanged() {

        val inputControl = presentationModel.inputControl()

        val testObserver = inputControl.focus.observable.test()

        pmTestHelper.setLifecycleTo(CREATED)

        inputControl.focusChanges.consumer.run {
            accept(true)
            accept(true)
            accept(false)
            accept(false)
            accept(true)
            accept(true)
        }

        testObserver
            .assertValues(
                false, // initial value
                true,
                false,
                true
            )
            .assertNoErrors()
    }
}