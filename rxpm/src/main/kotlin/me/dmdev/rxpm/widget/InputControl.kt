@file:Suppress("NOTHING_TO_INLINE")

package me.dmdev.rxpm.widget

import android.support.design.widget.TextInputLayout
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.widget.EditText
import com.jakewharton.rxbinding2.view.enabled
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import me.dmdev.rxpm.AndroidPmView
import me.dmdev.rxpm.PresentationModel

/**
 * Helps to bind a group of properties of an input field widget to a [presentation model][PresentationModel]
 * and also breaks the loop of two-way data binding to make the work with the input easier.
 *
 * You can bind this to an [EditText] or an [TextInputLayout] using the familiar `bindTo` methods
 * in the [AndroidPmView].
 *
 * Instantiate this using the [inputControl] extension function of the presentation model.
 *
 * @see CheckControl
 * @see ClickControl
 */
class InputControl internal constructor(pm: PresentationModel,
                                        initialText: String,
                                        initialEnabled: Boolean,
                                        formatter: (text: String) -> String,
                                        hideErrorOnUserInput: Boolean) {

    /**
     * The input field text [state][PresentationModel.State].
     */
    val text = pm.State(initialText)

    /**
     * The input field enabled [state][PresentationModel.State].
     */
    @Deprecated("Will be removed in 1.2")
    val enabled = pm.State(initialEnabled)

    /**
     * The input field error [state][PresentationModel.State].
     */
    val error = pm.State<String>()

    /**
     * The input field text changes [events][PresentationModel.Action].
     */
    val textChanges = pm.Action<String>()

    init {
        textChanges.relay
                .filter { it != text.value }
                .map { formatter.invoke(it) }
                .subscribe {
                    text.relay.accept(it)
                    if (hideErrorOnUserInput) error.relay.accept("")
                }
    }
}

/**
 * Creates the [InputControl].
 *
 * @param initialText initial text of the input field.
 * @param initialEnabled is input field initially enabled.
 * @param formatter formats the user input. The default does nothing.
 * @param hideErrorOnUserInput hide the error if user entered something.
 */
@Deprecated("Enabled state will be removed in 1.2", ReplaceWith("inputControl(initialText, formatter, hideErrorOnUserInput)", "me.dmdev.rxpm.widget.inputControl"))
fun PresentationModel.inputControl(initialText: String = "",
                                   initialEnabled: Boolean = true,
                                   formatter: (text: String) -> String = { it },
                                   hideErrorOnUserInput: Boolean = true): InputControl {
    return InputControl(this, initialText, initialEnabled, formatter, hideErrorOnUserInput)
}

/**
 * Creates the [InputControl].
 *
 * @param initialText initial text of the input field.
 * @param formatter formats the user input. The default does nothing.
 * @param hideErrorOnUserInput hide the error if user entered something.
 */
fun PresentationModel.inputControl(initialText: String = "",
                                   formatter: (text: String) -> String = { it },
                                   hideErrorOnUserInput: Boolean = true): InputControl {
    return InputControl(this, initialText, true, formatter, hideErrorOnUserInput)
}

inline internal fun TextInputLayout.bind(inputControl: InputControl): Disposable {
    val edit = editText!!
    return CompositeDisposable().apply {
        addAll(
                edit.bind(inputControl),
                inputControl.error.observable.subscribe { error ->
                    this@bind.error = if (error.isEmpty()) null else error
                }
        )
    }
}

inline internal fun EditText.bind(inputControl: InputControl): Disposable {

    return CompositeDisposable().apply {

        var editing = false
        addAll(
                inputControl.text.observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            val editable = text
                            if(!it.contentEquals(editable)) {
                                editing = true
                                if (editable is Spanned) {
                                    val ss = SpannableString(it)
                                    TextUtils.copySpansFrom(editable, 0, ss.length, null, ss, 0)
                                    editable.replace(0, editable.length, ss)
                                } else {
                                    editable.replace(0, editable.length, it)
                                }
                                editing = false
                            }
                        },

                inputControl.enabled.observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(enabled()),

                textChanges()
                        .skipInitialValue()
                        .filter { !editing }
                        .map { it.toString() }
                        .subscribe(inputControl.textChanges.consumer)
        )
    }
}