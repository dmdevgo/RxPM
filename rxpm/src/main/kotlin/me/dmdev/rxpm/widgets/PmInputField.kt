package ru.mobileup.yami.pm

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
    val textChangeConsumer: Consumer<String>
}

class PmInputField : InputField {

    val text = BehaviorRelay.createDefault<String>("")!!
    val enabled = BehaviorRelay.createDefault<Boolean>(true)!!
    val error = BehaviorRelay.create<String>()!!
    private val change = PublishRelay.create<String>()
    val textChanges = change.filter { it != text.value }!!

    var mapper: (text: String) -> String = { it }
    var validator: (text: String) -> String = { "" }

    override val textState = text.asObservable()
    override val enabledState = enabled.asObservable()
    override val errorState = error.asObservable()
    override val textChangeConsumer = change.asConsumer()

    init {
        textChanges
                .subscribe { error.accept("") }

        textChanges
                .map { mapper.invoke(it) }
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
                edit.textChanges().map { it.toString() }.subscribe(rxInputField.textChangeConsumer)
        )
    }
}