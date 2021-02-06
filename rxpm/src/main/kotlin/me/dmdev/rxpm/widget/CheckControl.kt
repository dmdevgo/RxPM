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

import android.widget.CompoundButton
import com.jakewharton.rxbinding2.widget.checkedChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import me.dmdev.rxpm.AndroidPmView
import me.dmdev.rxpm.PresentationModel

/**
 * Helps to bind a group of properties of a checkable widget to a [presentation model][PresentationModel]
 * and also breaks the loop of two-way data binding to make the work with the check easier.
 *
 * You can bind this to any [CompoundButton] subclass using the familiar `bindTo` method
 * in the [AndroidPmView].
 *
 * Instantiate this using the [checkControl] extension function of the presentation model.
 *
 * @see InputControl
 * @see DialogControl
 */
class CheckControl internal constructor(pm: PresentationModel, initialChecked: Boolean) {

    /**
     * The checked [state][PresentationModel.State].
     */
    val checked = pm.State(initialChecked)

    /**
     * The checked state change [events][PresentationModel.Action].
     */
    val checkedChanges = pm.Action<Boolean>()

    init {
        checkedChanges.relay
            .filter { it != checked.value }
            .subscribe(checked.relay)
    }
}

/**
 * Creates the [CheckControl].
 *
 * @param initialChecked initial checked state.
 */
fun PresentationModel.checkControl(initialChecked: Boolean = false): CheckControl {
    return CheckControl(this, initialChecked)
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun CheckControl.bind(
    compoundButton: CompoundButton,
    compositeDisposable: CompositeDisposable
) {

    var editing = false

    compositeDisposable.addAll(

        checked.observable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                editing = true
                compoundButton.isChecked = it
                editing = false
            },

        compoundButton.checkedChanges()
            .skipInitialValue()
            .filter { !editing }
            .subscribe(checkedChanges.consumer)
    )
}