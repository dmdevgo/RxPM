package me.dmdev.rxpm

import com.jakewharton.rxrelay2.*
import io.reactivex.android.schedulers.*
import io.reactivex.functions.*
import java.util.concurrent.atomic.*

/**
 * Reactive property for the [view's][PmView] state.
 * Can be observed and changed in reactive manner with it's [observable] and [PresentationModel.consumer].
 *
 * Use to represent a view state. It can be something simple, like some widget's text, or complex,
 * like inProgress or data.
 *
 * @see Action
 * @see Command
 */
class State<T> internal constructor(
    internal val pm: PresentationModel,
    initialValue: T? = null
) {

    internal val relay =
        if (initialValue != null) {
            BehaviorRelay.createDefault<T>(initialValue).toSerialized()
        } else {
            BehaviorRelay.create<T>().toSerialized()
        }

    private val cachedValue =
        if (initialValue != null) {
            AtomicReference<T?>(initialValue)
        } else {
            AtomicReference()
        }

    /**
     * Observable of this [State].
     */
    val observable = relay.asObservable()

    /**
     * Returns a current value.
     * @throws UninitializedPropertyAccessException if there is no value and [State] was created without `initialValue`.
     */
    val value: T
        get() {
            return cachedValue.get()
                ?: throw UninitializedPropertyAccessException("The State has no value yet. Use valueOrNull() or pass initialValue to the constructor.")
        }

    /**
     * Returns a current value or null.
     */
    val valueOrNull: T? get() = cachedValue.get()

    init {
        with(pm) {
            relay.subscribe { cachedValue.set(it) }
                .untilDestroy()
        }
    }

    /**
     * Returns true if the [State] has any value.
     */
    fun hasValue() = cachedValue.get() != null
}

/**
 * Creates the [State].
 *
 * @since 2.0
 */
fun <T> PresentationModel.state(initialValue: T? = null): State<T> {
    return State(this, initialValue)
}

/**
 * Subscribes to the [State][State] and adds it to the subscriptions list
 * that will be CLEARED ON [UNBIND][PresentationModel.Lifecycle.UNBINDED],
 * so use it ONLY in [PmView.onBindPresentationModel].
 *
 * @since 2.0
 */
infix fun <T> State<T>.bindTo(consumer: Consumer<in T>) {
    with(pm) {
        this@bindTo.observable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(consumer)
            .untilUnbind()
    }
}

/**
 * Subscribes to the [State][State] and adds it to the subscriptions list
 * that will be CLEARED ON [UNBIND][PresentationModel.Lifecycle.UNBINDED],
 * so use it ONLY in [PmView.onBindPresentationModel].
 *
 * @since 2.0
 */
infix fun <T> State<T>.bindTo(consumer: (T) -> Unit) {
    with(pm) {
        this@bindTo.observable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(consumer)
            .untilUnbind()
    }
}