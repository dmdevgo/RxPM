package me.dmdev.rxpm.widget

import android.app.Dialog
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.asObservable
import me.dmdev.rxpm.widget.State.Displayed
import me.dmdev.rxpm.widget.State.NotDisplayed

class DialogControl<T, R> internal constructor(pm: PresentationModel) {

    internal val state = pm.State<State>(NotDisplayed)
    private val result = pm.Action<R>()

    fun show(data: T): Maybe<R> {
        return result.relay.asObservable()
                .doOnSubscribe { state.relay.accept(Displayed(data)) }
                .takeUntil(state.relay.skip(1).filter { it == NotDisplayed })
                .firstElement()
    }

    fun sendResult(result: R) {
        this.result.consumer.accept(result)
    }

    fun dismiss() {
        state.relay.accept(NotDisplayed)
    }
}

internal sealed class State {
    class Displayed<T>(val data: T) : State()
    object NotDisplayed : State()
}

fun <T, R> PresentationModel.dialogControl(): DialogControl<T, R> {
    return DialogControl(this)
}

internal fun <T, R> DialogControl<T, R>.bind(f: (data: T, dc: DialogControl<T, R>) -> Dialog): Disposable {

    var dialog: Dialog? = null

    return state.observable
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                if (dialog?.isShowing == true) {
                    dialog?.setOnDismissListener(null)
                    dialog?.dismiss()
                    dialog = null
                }
            }
            .subscribe {
                if (it is Displayed<*>) {
                    @Suppress("UNCHECKED_CAST")
                    dialog = f.invoke(it.data as T, this@bind).apply {
                        this.setOnDismissListener {
                            this@bind.dismiss()
                        }
                        this.show()
                    }
                }
            }
}

