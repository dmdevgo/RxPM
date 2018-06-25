package me.dmdev.rxpm.widget

import android.app.Dialog
import android.app.DialogFragment
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import me.dmdev.rxpm.AndroidPmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.asObservable
import me.dmdev.rxpm.widget.DialogControl.State.Displayed
import me.dmdev.rxpm.widget.DialogControl.State.NotDisplayed

/**
 *
 * Helps to display a dialog and get the result in a reactive form.
 * Takes care of all lifecycle processing.
 *
 * The dialog attached using [AndroidPmView.bindTo] will be
 * automatically dismissed and restored on config changes ([UNBINDED][PresentationModel.Lifecycle.UNBINDED]
 * and [BINDED][PresentationModel.Lifecycle.BINDED] states correspondingly).
 * So there is no need to use [DialogFragment] or something similar.
 *
 * You can bind this to any subclass of [Dialog] using the familiar `bindTo` methods
 * in the [AndroidPmView].
 *
 * Instantiate this using the [dialogControl] extension function of the presentation model.
 *
 * @param T the type of the data to display in the dialog.
 * @param R the type of the result we get from the dialog.
 *
 * @see InputControl
 * @see CheckControl
 */
class DialogControl<T, R> internal constructor(pm: PresentationModel) {

    internal val displayed = pm.State<State>(NotDisplayed)
    private val result = pm.Action<R>()

    /**
     * Shows the dialog and waits for the result.
     *
     * @param data the data to display in the dialog.
     * @return [Maybe] that waits for the result of the dialog.
     */
    fun show(data: T): Maybe<R> {
        return result.relay.asObservable()
                .doOnSubscribe { displayed.relay.accept(Displayed(data)) }
                .takeUntil(
                        displayed.relay
                                .skip(1)
                                .filter { it == NotDisplayed }
                )
                .firstElement()
    }

    /**
     * Sends the [result] of the dialog and then dismisses the dialog.
     */
    fun sendResult(result: R) {
        this.result.consumer.accept(result)
        dismiss()
    }

    /**
     * Dismisses the dialog associated with this [DialogControl].
     */
    fun dismiss() {
        displayed.relay.accept(NotDisplayed)
    }

    internal sealed class State {
        class Displayed<T>(val data: T) : State()
        object NotDisplayed : State()
    }
}

/**
 * Creates the [DialogControl].
 *
 * @param T the type of the data to display in the dialog.
 * @param R the type of the result we get from the dialog.
 */
fun <T, R> PresentationModel.dialogControl(): DialogControl<T, R> {
    return DialogControl(this)
}

internal inline fun <T, R> DialogControl<T, R>.bind(crossinline createDialog: (data: T, dc: DialogControl<T, R>) -> Dialog): Disposable {

    var dialog: Dialog? = null

    return displayed.observable
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                if (dialog?.isShowing == true) {
                    dialog?.setOnDismissListener(null)
                    dialog?.dismiss()
                    dialog = null
                }
            }
            .subscribe {
                @Suppress("UNCHECKED_CAST")
                if (it is Displayed<*>) {
                    dialog = createDialog(it.data as T, this)
                    dialog?.setOnDismissListener { this.dismiss() }
                    dialog?.show()
                }
            }
}

