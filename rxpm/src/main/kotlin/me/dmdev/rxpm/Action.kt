package me.dmdev.rxpm

import android.annotation.*
import com.jakewharton.rxrelay2.*
import io.reactivex.*
import io.reactivex.android.schedulers.*

/**
 * Reactive property for the actions from the [view][PmView].
 * Can be changed and observed in reactive manner with it's [consumer] and [PresentationModel.observable].
 *
 * Use to send actions of the view, e.g. some widget's clicks.
 *
 * @see State
 * @see Command
 */
class Action<T> internal constructor(internal val pm: PresentationModel) {

    internal val relay = PublishRelay.create<T>().toSerialized()

    /**
     * Consumer of the [Action][Action].
     */
    val consumer get() = relay.asConsumer()
}

/**
 * Creates the [Action].
 * Optionally subscribes the [action chain][actionChain] to this action.
 * This chain will be unsubscribed ON [DESTROY][PresentationModel.Lifecycle.DESTROYED].
 */
@SuppressLint("CheckResult")
fun <T> PresentationModel.action(
    actionChain: (Observable<T>.() -> Observable<*>)? = null
): Action<T> {
    val action = Action<T>(pm = this)

    if (actionChain != null) {
        lifecycleObservable
            .filter { it == PresentationModel.Lifecycle.CREATED }
            .take(1)
            .subscribe {
                actionChain.let { chain ->
                    action.relay
                        .chain()
                        .retry()
                        .subscribe()
                        .untilDestroy()
                }
            }
    }

    return action
}

/**
 * Subscribes [Action][Action] to the observable and adds it to the subscriptions list
 * that will be CLEARED ON [UNBIND][PresentationModel.Lifecycle.UNBINDED],
 * so use it ONLY in [PmView.onBindPresentationModel].
 */
infix fun <T> Observable<T>.bindTo(action: Action<T>) {
    with(action.pm) {
        this@bindTo.observeOn(AndroidSchedulers.mainThread())
            .subscribe(action.consumer)
            .untilUnbind()
    }
}

/**
 * Pass the value to the [Action][Action].
 */
infix fun <T> T.passTo(action: Action<T>) {
    action.consumer.accept(this)
}

/**
 * Pass an empty value to the [Action][Action].
 */
infix fun Unit.passTo(action: Action<Unit>) {
    action.consumer.accept(Unit)
}