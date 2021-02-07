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

import android.widget.*
import com.jakewharton.rxbinding3.widget.*
import me.dmdev.rxpm.*

/**
 * Helps to bind a group of properties of a checkable widget to a [presentation model][PresentationModel]
 * and also breaks the loop of two-way data binding to make the work with the check easier.
 *
 * You can bind this to any [CompoundButton] subclass using the [bindTo][bindTo] extension.
 *
 * Instantiate this using the [checkControl] extension function of the presentation model.
 *
 * @see InputControl
 * @see DialogControl
 */
class CheckControl internal constructor(initialChecked: Boolean) : PresentationModel() {

    /**
     * The checked [state][State].
     */
    val checked = state(initialChecked)

    /**
     * The checked state change [events][Action].
     */
    val checkedChanges = action<Boolean>()

    override fun onCreate() {
        super.onCreate()
        checkedChanges.observable
            .filter { it != checked.value }
            .subscribe(checked.consumer)
            .untilDestroy()
    }
}

/**
 * Creates the [CheckControl].
 *
 * @param initialChecked initial checked state.
 */
fun PresentationModel.checkControl(initialChecked: Boolean = false): CheckControl {
    return CheckControl(initialChecked).apply {
        attachToParent(this@checkControl)
    }
}

/**
 * Binds the [CheckControl] to the [CompoundButton][compoundButton], use it ONLY in [PmView.onBindPresentationModel].
 */
infix fun CheckControl.bindTo(compoundButton: CompoundButton) {

    var editing = false

    checked bindTo {
        editing = true
        compoundButton.isChecked = it
        editing = false
    }

    compoundButton.checkedChanges()
        .skipInitialValue()
        .filter { !editing && it != checked.value }
        .bindTo(checkedChanges)
}