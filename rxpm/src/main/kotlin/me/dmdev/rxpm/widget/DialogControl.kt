package me.dmdev.rxpm.widget

import android.app.Dialog
import android.app.DialogFragment
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import me.dmdev.rxpm.AndroidPmView
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.widget.DialogControl.Display.Absent
import me.dmdev.rxpm.widget.DialogControl.Display.Displayed

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
 *
 * @since 1.2
 */
class DialogControl<T, R> internal constructor(): PresentationModel() {

    val displayed = State<Display>(Absent)
    private val result = Action<R>()

    /**
     * Shows the dialog.
     *
     * @param data the data to display in the dialog.
     */
    fun show(data: T) {
        dismiss()
        displayed.consumer.accept(Displayed(data))
    }

    /**
     * Shows the dialog and waits for the result.
     *
     * @param data the data to display in the dialog.
     * @return [Maybe] that waits for the result of the dialog.
     */
    fun showForResult(data: T): Maybe<R> {

        dismiss()

        return result.observable
            .doOnSubscribe {
                displayed.consumer.accept(Displayed(data))
            }
            .takeUntil(
                displayed.observable
                    .skip(1)
                    .filter { it == Absent }
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
        if (displayed.valueOrNull is Displayed<*>) {
            displayed.consumer.accept(Absent)
        }
    }

    sealed class Display {
        data class Displayed<T>(val data: T) : Display()
        object Absent : Display()
    }
}

/**
 * Creates the [DialogControl].
 *
 * @param T the type of the data to display in the dialog.
 * @param R the type of the result we get from the dialog.
 *
 * @since 1.2
 */
fun <T, R> PresentationModel.dialogControl(): DialogControl<T, R> {
    return DialogControl<T, R>().apply {
        attachToParent(this@dialogControl)
    }
}

/**
 * Binds the [DialogControl] to the [Dialog], use it ONLY in [PmView.onBindPresentationModel].
 * @param createDialog function that creates [Dialog] using passed data.
 *
 * @since 2.0
 */
inline infix fun <T, R> DialogControl<T, R>.bindTo(crossinline createDialog: (data: T, dc: DialogControl<T, R>) -> Dialog) {

    var dialog: Dialog? = null

    val closeDialog: () -> Unit = {
        dialog?.setOnDismissListener(null)
        dialog?.dismiss()
        dialog = null
    }

    displayed.observable
        .observeOn(AndroidSchedulers.mainThread())
        .doFinally { closeDialog() }
        .subscribe {
            @Suppress("UNCHECKED_CAST")
            if (it is Displayed<*>) {
                dialog = createDialog(it.data as T, this)
                dialog?.setOnDismissListener { this.dismiss() }
                dialog?.show()
            } else if (it === Absent) {
                closeDialog()
            }
        }
        .untilUnbind()

}

