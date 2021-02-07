/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2021 Dmitriy Gorbunov (dmitriy.goto@gmail.com)
 *                     and Vasili Chyrvon (vasili.chyrvon@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.dmdev.rxpm.widget

import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.widget.DialogControl.Display.Absent
import me.dmdev.rxpm.widget.DialogControl.Display.Displayed
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