package me.dmdev.rxpm.widget

import android.support.design.widget.TextInputLayout
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import com.jakewharton.rxbinding2.view.enabled
import com.jakewharton.rxbinding2.widget.textChanges
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import me.dmdev.rxpm.asConsumer
import me.dmdev.rxpm.asObservable


/**
 * @author Dmitriy Gorbunov
 */
interface InputField {
    val textState: Observable<String>
    val enabledState: Observable<Boolean>
    val errorState: Observable<String>
    val textChangesConsumer: Consumer<String>
}

class PmInputField(
        initialText: String = "",
        initialEnabled: Boolean = true,
        val formatter: (text: String) -> String = { it },
        val validator: (text: String) -> String = { "" }

) : InputField {

    val text = BehaviorRelay.createDefault<String>(initialText)!!
    val enabled = BehaviorRelay.createDefault<Boolean>(initialEnabled)!!
    val error = BehaviorRelay.create<String>()!!
    private val changes = PublishRelay.create<String>()
    val textChanges = changes.filter { it != text.value }!!

    override val textState = text.asObservable()
    override val enabledState = enabled.asObservable()
    override val errorState = error.asObservable()
    override val textChangesConsumer = changes.asConsumer()

    init {
        textChanges.subscribe { error.accept("") }

        textChanges
                .map { formatter.invoke(it) }
                .subscribe(text)
    }

    fun validate() {
        error.accept(validator.invoke(text.value))
    }
}

fun TextInputLayout.bind(rxInputField: InputField): Disposable {
    val edit = editText!!
    return CompositeDisposable().apply {
        addAll(
                rxInputField.textState.subscribe {
                    val editable = edit.text

                    if (editable is Spanned) {
                        val ss = SpannableString(it)
                        TextUtils.copySpansFrom(editable, 0, ss.length, null, ss, 0)
                        editable.replace(0, editable.length, ss)
                    } else {
                        editable.replace(0, editable.length, it)
                    }
                },
                rxInputField.enabledState.subscribe(edit.enabled()),
                rxInputField.errorState.subscribe { error ->
                    this@bind.error = if (error.isEmpty()) null else error
                },
                edit.textChanges().map { it.toString() }.subscribe(rxInputField.textChangesConsumer)
        )
    }
}