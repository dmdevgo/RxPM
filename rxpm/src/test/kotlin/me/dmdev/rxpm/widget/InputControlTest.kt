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
import me.dmdev.rxpm.PresentationModel.Lifecycle.CREATED
import me.dmdev.rxpm.test.PmTestHelper
import org.junit.Before
import org.junit.Test

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