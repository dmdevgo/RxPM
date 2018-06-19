package me.dmdev.rxpm.widget

import android.app.Dialog
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.asObservable
import me.dmdev.rxpm.widget.DialogControl.State.Displayed
import me.dmdev.rxpm.widget.DialogControl.State.NotDisplayed

class DialogControl<T, R> internal constructor(pm: PresentationModel) {

    internal val displayed = pm.State<State>(NotDisplayed)
    private val result = pm.Action<R>()

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

    fun sendResult(result: R) {
        this.result.consumer.accept(result)
        dismiss()
    }

    fun dismiss() {
        displayed.relay.accept(NotDisplayed)
    }

    internal sealed class State {
        class Displayed<T>(val data: T) : State()
        object NotDisplayed : State()
    }
}

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

