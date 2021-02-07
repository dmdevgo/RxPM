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

import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding3.view.focusChanges
import com.jakewharton.rxbinding3.widget.textChanges
import me.dmdev.rxpm.*

/**
 * Helps to bind a group of properties of an input field widget to a [presentation model][PresentationModel]
 * and also breaks the loop of two-way data binding to make the work with the input easier.
 *
 * You can bind this to an [EditText] or an [TextInputLayout] using the [bindTo][bindTo] extension.
 *
 * Instantiate this using the [inputControl] extension function of the presentation model.
 *
 * @see CheckControl
 * @see DialogControl
 */
class InputControl internal constructor(
    initialText: String,
    private val formatter: ((text: String) -> String)?,
    private val hideErrorOnUserInput: Boolean = true
) : PresentationModel() {

    /**
     * The input field text [state][State].
     */
    val text = state(initialText, diffStrategy = null)

    /**
     * The input field error [state][State].
     */
    val error = state<String>(diffStrategy = null)

    /**
     * The input field focus [state][State].
     */
    val focus = state<Boolean>(initialValue = false, diffStrategy = null)

    /**
     * The input field text changes [events][Action].
     */
    val textChanges = action<String>()

    /**
     * The input field focus changes [events][Action].
     */
    val focusChanges = action<Boolean>()

    override fun onCreate() {

        if (formatter != null) {
            textChanges.observable
                .map { formatter.invoke(it) }
                .subscribe {
                    text.consumer.accept(it)
                    if (hideErrorOnUserInput) error.consumer.accept("")
                }
                .untilDestroy()
        }

        focusChanges.observable
            .distinctUntilChanged()
            .filter { it != focus.valueOrNull }
            .subscribe(focus)
            .untilDestroy()
    }
}

/**
 * Creates the [InputControl].
 *
 * @param initialText initial text of the input field.
 * @param formatter formats the user input. The default does nothing.
 * @param hideErrorOnUserInput hide the error if user entered something.
 */
fun PresentationModel.inputControl(
    initialText: String = "",
    formatter: ((text: String) -> String)? = { it },
    hideErrorOnUserInput: Boolean = true
): InputControl {
    return InputControl(initialText, formatter, hideErrorOnUserInput).apply {
        attachToParent(this@inputControl)
    }
}

/**
 * Binds the [InputControl] to the [TextInputLayout][textInputLayout], use it ONLY in [PmView.onBindPresentationModel].
 */
infix fun InputControl.bindTo(textInputLayout: TextInputLayout) {

    bindTo(textInputLayout.editText!!)

    error bindTo { error ->
        textInputLayout.error = if (error.isEmpty()) null else error
    }
}

/**
 * Binds the [InputControl] to the [EditText][editText], use it ONLY in [PmView.onBindPresentationModel].
 */
infix fun InputControl.bindTo(editText: EditText) {

    var editing = false

    text bindTo {
        val editable = editText.text
        if (!it.contentEquals(editable)) {
            editing = true
            if (editable is Spanned) {
                val ss = SpannableString(it)
                TextUtils.copySpansFrom(editable, 0, ss.length, null, ss, 0)
                editable.replace(0, editable.length, ss)

                val selection = editText.selectionStart
                editText.text = editable
                editText.setSelection(selection)
            } else {
                editable.replace(0, editable.length, it)
            }
            editing = false
        }
    }

    focus bindTo {
        if (it && !editText.hasFocus()) {
            editText.requestFocus()
        }
    }

    editText.textChanges()
        .skipInitialValue()
        .filter { !editing && text.valueOrNull?.contentEquals(it) != true }
        .map { it.toString() }
        .bindTo(textChanges)

    editText.focusChanges().bindTo(focusChanges)
}