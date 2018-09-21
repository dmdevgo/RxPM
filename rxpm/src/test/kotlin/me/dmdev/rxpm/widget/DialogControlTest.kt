package me.dmdev.rxpm.widget

import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.widget.DialogControl.Display.Displayed
import me.dmdev.rxpm.widget.DialogControl.Display.Absent
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class DialogControlTest {

    private lateinit var dialogControl: DialogControl<Unit, Unit>

    @Before fun setUp() {
        dialogControl = createDialogControl()
    }

    private fun createDialogControl(): DialogControl<Unit, Unit> {
        val pm = object : PresentationModel() {}
        return pm.dialogControl()
    }

    @Test fun displayedOnShow() {
        dialogControl.showForResult(Unit).subscribe()
        assertTrue { dialogControl.displayed.value is Displayed<*> }
    }

    @Test fun removedOnResult() {
        dialogControl.showForResult(Unit).subscribe()
        dialogControl.sendResult(Unit)
        assertTrue { dialogControl.displayed.value === Absent }
    }

    @Test fun acceptOneResult() {
        val testObserver = dialogControl.showForResult(Unit).test()

        // When two results sent
        dialogControl.sendResult(Unit)
        dialogControl.sendResult(Unit)

        // Then only one is here
        testObserver.assertResult(Unit)
    }

    @Test fun removedOnDismiss() {
        dialogControl.showForResult(Unit).subscribe()
        dialogControl.dismiss()
        assertTrue { dialogControl.displayed.value === Absent }
    }

    @Test fun cancelDialog() {
        val testObserver = dialogControl.showForResult(Unit).test()
        dialogControl.dismiss()

        testObserver
            .assertSubscribed()
            .assertNoValues()
            .assertNoErrors()
            .assertComplete()
    }

    @Test fun dismissPreviousOnNewShow() {
        val displayedObserver = dialogControl.displayed.observable.test()

        val firstObserver = dialogControl.showForResult(Unit).test()
        val secondObserver = dialogControl.showForResult(Unit).test()

        displayedObserver
            .assertSubscribed()
            .assertValueCount(4)
            .assertValueAt(0, Absent)
            .assertValueAt(1) { it is Displayed<*> }
            .assertValueAt(2, Absent)
            .assertValueAt(3) { it is Displayed<*> }
            .assertNoErrors()

        firstObserver
            .assertSubscribed()
            .assertNoValues()
            .assertNoErrors()
            .assertComplete()

        secondObserver
            .assertEmpty()
    }
}