package me.dmdev.rxpm

import android.support.design.widget.TextInputLayout
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import me.dmdev.rxpm.widget.CheckControl
import me.dmdev.rxpm.widget.ClickControl
import me.dmdev.rxpm.widget.InputControl
import me.dmdev.rxpm.widget.bind

/**
 * @author Dmitriy Gorbunov
 */
interface AndroidPmView<PM : PresentationModel> : PmView<PM> {

    /**
     * Local extension to subscribe to the observable and add it to the subscriptions list
     * that will be CLEARED [ON UNBIND][compositeUnbind], so use it ONLY in [onBindPresentationModel].
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
     * that will be CLEARED [ON UNBIND][compositeUnbind], so use it ONLY in [onBindPresentationModel].
     */
    infix fun <T> Observable<T>.bindTo(consumer: (T) -> Unit) {
        compositeUnbind.add(
                this
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(consumer)
        )
    }

    /**
     * Local extension to bind [InputControl] to [EditText], use it ONLY in [onBindPresentationModel].
     */
    infix fun InputControl.bindTo(editText: EditText) {
        compositeUnbind.add(editText.bind(this))
    }

    /**
     * Local extension to bind [InputControl] to [TextInputLayout], use it ONLY in [onBindPresentationModel].
     */
    infix fun InputControl.bindTo(textInputLayout: TextInputLayout) {
        compositeUnbind.add(textInputLayout.bind(this))
    }

    /**
     * Local extension to bind [CheckControl] to [CompoundButton], use it ONLY in [onBindPresentationModel].
     */
    infix fun CheckControl.bindTo(compoundButton: CompoundButton) {
        compositeUnbind.add(compoundButton.bind(this))
    }

    /**
     * Local extension to bind [ClickControl] to [View], use it ONLY in [onBindPresentationModel].
     */
    infix fun ClickControl.bindTo(view: View) {
        compositeUnbind.add(view.bind(this))
    }

    /**
     * Local extension to pass the empty value to the [Consumer].
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