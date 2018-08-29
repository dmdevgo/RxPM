package me.dmdev.rxpm

import android.app.Dialog
import android.support.design.widget.TextInputLayout
import android.widget.CompoundButton
import android.widget.EditText
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import me.dmdev.rxpm.widget.CheckControl
import me.dmdev.rxpm.widget.DialogControl
import me.dmdev.rxpm.widget.InputControl
import me.dmdev.rxpm.widget.bind

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
        this.observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer)
                .untilUnbind()
    }

    /**
     * Local extension to subscribe to the observable and add it to the subscriptions list
     * that will be CLEARED ON [UNBIND][PresentationModel.Lifecycle.UNBINDED],
     * so use it ONLY in [onBindPresentationModel].
     */
    infix fun <T> Observable<T>.bindTo(consumer: (T) -> Unit) {
        this.observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer)
                .untilUnbind()
    }

    /**
     * Local extension to subscribe [Action][PresentationModel.Action] to the observable and add it to the subscriptions list
     * that will be CLEARED ON [UNBIND][PresentationModel.Lifecycle.UNBINDED],
     * so use it ONLY in [onBindPresentationModel].
     *
     * @since 1.2
     */
    infix fun <T> Observable<T>.bindTo(action: PresentationModel.Action<T>) {
        this.observeOn(AndroidSchedulers.mainThread())
                .subscribe(action.consumer)
                .untilUnbind()
    }

    /**
     * Local extension to subscribe to the [State][PresentationModel.State] and add it to the subscriptions list
     * that will be CLEARED ON [UNBIND][PresentationModel.Lifecycle.UNBINDED],
     * so use it ONLY in [onBindPresentationModel].
     *
     * @since 1.2
     */
    infix fun <T> PresentationModel.State<T>.bindTo(consumer: Consumer<in T>) {
        this.observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer)
                .untilUnbind()
    }


    /**
     * Local extension to subscribe to the [State][PresentationModel.State] and add it to the subscriptions list
     * that will be CLEARED ON [UNBIND][PresentationModel.Lifecycle.UNBINDED],
     * so use it ONLY in [onBindPresentationModel].
     *
     * @since 1.2
     */
    infix fun <T> PresentationModel.State<T>.bindTo(consumer: (T) -> Unit) {
        this.observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer)
                .untilUnbind()
    }

    /**
     * Local extension to subscribe to the [Command][PresentationModel.Command] and add it to the subscriptions list
     * that will be CLEARED ON [UNBIND][PresentationModel.Lifecycle.UNBINDED],
     * so use it ONLY in [onBindPresentationModel].
     *
     * @since 1.2
     */
    infix fun <T> PresentationModel.Command<T>.bindTo(consumer: Consumer<in T>) {
        this.observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer)
                .untilUnbind()
    }

    /**
     * Local extension to subscribe to the [Command][PresentationModel.Command] and add it to the subscriptions list
     * that will be CLEARED ON [UNBIND][PresentationModel.Lifecycle.UNBINDED],
     * so use it ONLY in [onBindPresentationModel].
     *
     * @since 1.2
     */
    infix fun <T> PresentationModel.Command<T>.bindTo(consumer: (T) -> Unit) {
        this.observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer)
                .untilUnbind()
    }

    /**
     * Local extension to bind the [InputControl] to the [EditText][editText], use it ONLY in [onBindPresentationModel].
     */
    infix fun InputControl.bindTo(editText: EditText) {
        bind(editText, compositeUnbind)
    }

    /**
     * Local extension to bind the [InputControl] to the [TextInputLayout][textInputLayout], use it ONLY in [onBindPresentationModel].
     */
    infix fun InputControl.bindTo(textInputLayout: TextInputLayout) {
        bind(textInputLayout, compositeUnbind)
    }

    /**
     * Local extension to bind the [CheckControl] to the [CompoundButton][compoundButton], use it ONLY in [onBindPresentationModel].
     */
    infix fun CheckControl.bindTo(compoundButton: CompoundButton) {
        bind(compoundButton, compositeUnbind)
    }

    /**
     * Local extension to bind the [DialogControl] to the [Dialog], use it ONLY in [onBindPresentationModel].
     * @param createDialog function that creates [Dialog] using passed data.
     *
     * @since 1.2
     */
    infix fun <T, R> DialogControl<T, R>.bindTo(createDialog: (data: T, dc: DialogControl<T, R>) -> Dialog) {
        bind({ data, dc -> createDialog(data, dc) }, compositeUnbind)
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

    /**
     * Local extension to pass the value to the [Action][PresentationModel.Action].
     *
     * @since 1.2
     */
    infix fun <T> T.passTo(action: PresentationModel.Action<T>) {
        action.consumer.accept(this)
    }

}