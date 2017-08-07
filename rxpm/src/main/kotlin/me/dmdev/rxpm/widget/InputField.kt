package me.dmdev.rxpm.widget

import android.support.design.widget.TextInputLayout
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import com.jakewharton.rxbinding2.view.enabled
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import me.dmdev.rxpm.PresentationModel.Action
import me.dmdev.rxpm.PresentationModel.State

/**
 * @author Dmitriy Gorbunov
 */
class InputField(initialText: String = "",
                 initialEnabled: Boolean = true,
                 val formatter: (text: String) -> String = { it },
                 val validator: (text: String) -> String = { "" },
                 val hideErrorOnUserInput: Boolean = true) {

    val text = State(initialText)
    val enabled = State(initialEnabled)
    val error = State<String>()
    val textChanges = Action<String>()

    init {
        textChanges.relay
                .filter { it != text.value }
                .map { formatter.invoke(it) }
                .subscribe {
                    text.relay.accept(it)
                    if (hideErrorOnUserInput) error.relay.accept("")
                }
    }

    fun validate() {
        error.relay.accept(validator.invoke(text.value!!))
    }
}

fun TextInputLayout.bind(inputField: InputField): Disposable {
    val edit = editText!!
    return CompositeDisposable().apply {
        addAll(
                inputField.text.observable.subscribe {
                    val editable = edit.text

                    if (editable is Spanned) {
                        val ss = SpannableString(it)
                        TextUtils.copySpansFrom(editable, 0, ss.length, null, ss, 0)
                        editable.replace(0, editable.length, ss)
                    } else {
                        editable.replace(0, editable.length, it)
                    }
                },
                inputField.enabled.observable.subscribe(edit.enabled()),
                inputField.error.observable.subscribe { error ->
                    this@bind.error = if (error.isEmpty()) null else error
                },
                edit.textChanges().map { it.toString() }.subscribe(inputField.textChanges.consumer)
        )
    }
}