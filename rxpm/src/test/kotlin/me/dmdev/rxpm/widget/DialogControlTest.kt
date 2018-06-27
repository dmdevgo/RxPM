package me.dmdev.rxpm.widget

import io.reactivex.observers.TestObserver
import junit.framework.Assert.assertEquals
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.widget.DialogControl.State.Displayed
import me.dmdev.rxpm.widget.DialogControl.State.NotDisplayed
import org.junit.Test

class DialogControlTest {

    @Test
    fun simpleDialogResultTest() {

        val pm = object : PresentationModel() {}
        val dc = pm.dialogControl<Unit, Unit>()

        val to = TestObserver<Unit>()

        dc.show(Unit).subscribe(to)

        assertEquals(true, dc.displayed.value is Displayed<*>)

        dc.sendResult(Unit)
        dc.sendResult(Unit) // only one is expected

        assertEquals(true, dc.displayed.value === NotDisplayed)

        to.assertResult(Unit)
    }

    @Test
    fun cancelDialogTest() {

        val pm = object : PresentationModel() {}
        val dc = pm.dialogControl<Unit, Unit>()

        val to = TestObserver<Unit>()

        dc.show(Unit).subscribe(to)

        assertEquals(true, dc.displayed.value is Displayed<*>)

        dc.dismiss()

        assertEquals(true, dc.displayed.value === NotDisplayed)

        to.assertSubscribed()
        to.assertNoErrors()
        to.assertNoValues()
        to.assertComplete()
    }

    @Test
    fun onlyOneActiveDialogTest() {

        val pm = object : PresentationModel() {}
        val dc = pm.dialogControl<Unit, Unit>()

        val to = TestObserver<DialogControl.State>()
        val firstResultObserver = TestObserver<Unit>()
        val secondResultObserver = TestObserver<Unit>()

        dc.displayed.observable.subscribe(to)

        dc.show(Unit).subscribe(firstResultObserver)
        dc.show(Unit).subscribe(secondResultObserver)

        to.assertSubscribed()
        to.assertNoErrors()
        to.assertValueCount(4)

        to.assertValueAt(0, NotDisplayed)
        to.assertValueAt(1) { it is Displayed<*> }
        to.assertValueAt(2, NotDisplayed)
        to.assertValueAt(3) { it is Displayed<*> }

        firstResultObserver
                .assertSubscribed()
                .assertComplete()

        secondResultObserver.assertEmpty()

    }
}