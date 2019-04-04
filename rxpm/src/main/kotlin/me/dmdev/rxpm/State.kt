package me.dmdev.rxpm

import com.jakewharton.rxrelay2.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.*
import io.reactivex.functions.*
import io.reactivex.schedulers.Schedulers

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
class State<T> constructor(
    internal val pm: PresentationModel,
    initialValue: T? = null,
    private val diffStrategy: DiffStrategy<T>? = null
) {

    private val behaviorRelay: BehaviorRelay<T>

    internal val relay =
        if (initialValue != null) {
            behaviorRelay = BehaviorRelay.createDefault<T>(initialValue)
            behaviorRelay.toSerialized()
        } else {
            behaviorRelay = BehaviorRelay.create<T>()
            behaviorRelay.toSerialized()
        }

    /**
     * Observable of this [State].
     */
    val observable: Observable<T>
        get() {
            return if (diffStrategy != null) {
                if (diffStrategy.isAsync()) {
                    relay
                        .observeOn(Schedulers.computation())
                        .distinctUntilChanged(diffStrategy::isTheSame)
                } else {
                    relay.distinctUntilChanged(diffStrategy::isTheSame)
                }
            } else {
                relay.asObservable()
            }
        }

    /**
     * Returns a current value.
     * @throws UninitializedPropertyAccessException if there is no value and [State] was created without `initialValue`.
     */
    val value: T
        get() {
            return behaviorRelay.value
                ?: throw UninitializedPropertyAccessException("The State has no value yet. Use valueOrNull() or pass initialValue to the constructor.")
        }

    /**
     * Returns a current value or null.
     */
    val valueOrNull: T? get() = behaviorRelay.value


    /**
     * Returns true if the [State] has any value.
     */
    fun hasValue() = behaviorRelay.hasValue()
}

/**
 * Creates the [State].
 *
 * todo doc
 *
 * @since 2.0
 */
inline fun <reified T> PresentationModel.state(
    initialValue: T? = null,
    diffStrategy: DiffStrategy<T>? = DefaultDiffStrategy()
): State<T> {
    return State(this, initialValue, diffStrategy)
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

/**
 * todo doc
 *
 * @since 2.0
 */
interface DiffStrategy<T> {

    fun isTheSame(new: T, old: T): Boolean

    fun isAsync(): Boolean
}

/**
 * todo doc
 *
 * @since 2.0
 */
class DefaultDiffStrategy<T> : DiffStrategy<T> {

    override fun isTheSame(new: T, old: T) = new == old

    override fun isAsync() = false
}