package me.dmdev.rxpm

import android.app.Dialog
import android.support.design.widget.TextInputLayout
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import me.dmdev.rxpm.widget.*

/**
 * Extends [PmView] by adding several useful extensions.
 */
interface AndroidPmView<PM : PresentationModel> : PmView<PM> {

    /**
     * Local extension to subscribe to the observable and add it to the subscriptions list
     * that will be CLEARED ON [UNBIND][PresentationModel.Lifecycle.UNBINDED],
     * so use it ONLY in [onBindPresentationModel].
     */
    infix fun <T> Observable<T>.bindTo(consumer: Consumer<in T>) {
        compositeUnbind.add(
                this
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(consumer)
        )
    }

    /**
     * Local extension to subscribe to the observable and add it to the subscriptions list
     * that will be CLEARED ON [UNBIND][PresentationModel.Lifecycle.UNBINDED],
     * so use it ONLY in [onBindPresentationModel].
     */
    infix fun <T> Observable<T>.bindTo(consumer: (T) -> Unit) {
        compositeUnbind.add(
                this
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(consumer)
        )
    }

    /**
     * Local extension to bind the [InputControl] to the [EditText][editText], use it ONLY in [onBindPresentationModel].
     */
    infix fun InputControl.bindTo(editText: EditText) {
        compositeUnbind.add(editText.bind(this))
    }

    /**
     * Local extension to bind the [InputControl] to the [TextInputLayout][textInputLayout], use it ONLY in [onBindPresentationModel].
     */
    infix fun InputControl.bindTo(textInputLayout: TextInputLayout) {
        compositeUnbind.add(textInputLayout.bind(this))
    }

    /**
     * Local extension to bind the [CheckControl] to the [CompoundButton][compoundButton], use it ONLY in [onBindPresentationModel].
     */
    infix fun CheckControl.bindTo(compoundButton: CompoundButton) {
        compositeUnbind.add(compoundButton.bind(this))
    }

    /**
     * Local extension to bind the [ClickControl] to the [View][view], use it ONLY in [onBindPresentationModel].
     */
    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Will be removed in 1.2")
    infix fun ClickControl.bindTo(view: View) {
        compositeUnbind.add(view.bind(this))
    }

    infix fun <T, R> DialogControl<T, R>.bindTo(f: (data: T, dc: DialogControl<T, R>) -> Dialog) {
        compositeUnbind.add(
                bind { data, dc -> f(data, dc) }
        )
    }

    /**
     * Local extension to pass an empty value to the [Consumer].
     */
    infix fun passTo(consumer: Consumer<Unit>) {
        consumer.accept(Unit)
    }

    /**
     * Local extension to pass the value to the [Consumer].
     */
    infix fun <T> T.passTo(consumer: Consumer<T>) {
        consumer.accept(this)
    }

}