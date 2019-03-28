package me.dmdev.rxpm

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import me.dmdev.rxpm.navigation.NavigationMessage
import me.dmdev.rxpm.navigation.NavigationMessageHandler
import java.util.concurrent.atomic.AtomicReference

/**
 * Parent class for any Presentation Model.
 */
abstract class PresentationModel {

    enum class Lifecycle {
        CREATED, BINDED, UNBINDED, DESTROYED
    }

    private val compositeDestroy = CompositeDisposable()
    private val compositeUnbind = CompositeDisposable()

    private val lifecycle = BehaviorRelay.create<Lifecycle>()
    private val unbind = BehaviorRelay.createDefault<Boolean>(true)

    /**
     * Command to send [navigation message][NavigationMessage] to the [NavigationMessageHandler].
     * @since 1.1
     */
    val navigationMessages = Command<NavigationMessage>()

    /**
     * The [lifecycle][Lifecycle] of this presentation model.
     * @since 1.1
     */
    val lifecycleObservable = lifecycle.asObservable()
    internal val lifecycleConsumer = lifecycle.asConsumer()

    /**
     * Current state of this presentation model lifecycle.
     *
     * @return [lifecycle state][Lifecycle] or null if this presentation model is not created yet.
     * @since 1.2
     */
    val currentLifecycleState: Lifecycle? get() = lifecycle.value

    init {
        lifecycle
            .takeUntil { it == Lifecycle.DESTROYED }
            .subscribe {
                @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
                when (it) {
                    Lifecycle.CREATED -> onCreate()
                    Lifecycle.BINDED -> {
                        unbind.accept(false)
                        onBind()
                    }
                    Lifecycle.UNBINDED -> {
                        unbind.accept(true)
                        compositeUnbind.clear()
                        onUnbind()
                    }
                    Lifecycle.DESTROYED -> {
                        compositeDestroy.clear()
                        onDestroy()
                    }
                }
            }
    }

    /**
     * Called when the presentation model is created.
     * @see [onBind]
     * @see [onUnbind]
     * @see [onDestroy]
     */
    protected open fun onCreate() {}

    /**
     * Called when the presentation model binds to the [view][PmView].
     * @see [onCreate]
     * @see [onUnbind]
     * @see [onDestroy]
     */
    protected open fun onBind() {}

    /**
     * Called when the presentation model unbinds from the [view][PmView].
     * @see [onCreate]
     * @see [onBind]
     * @see [onDestroy]
     */
    protected open fun onUnbind() {}

    /**
     * Called just before the presentation model will be destroyed.
     * @see [onCreate]]
     * @see [onBind]
     * @see [onUnbind]
     */
    protected open fun onDestroy() {}

    /**
     * Attaches `this` (child presentation model) to the [parent] presentation model.
     * This presentation model will be bind to the lifecycle of the [parent] presentation model.
     *
     * @see [detachFromParent]
     * @since 1.1.2
     */
    fun attachToParent(parent: PresentationModel) {

        if (parent == this) {
            throw IllegalArgumentException("Presentation model can't be attached to itself.")
        }

        if (lifecycle.hasValue()) {
            throw IllegalStateException("Presentation model can't be a child more than once. It must not be reused.")
        }

        when (parent.lifecycle.value) {

            Lifecycle.BINDED -> {
                parent.lifecycleObservable
                    .startWith(Lifecycle.CREATED)
                    .subscribe(lifecycleConsumer)
            }

            Lifecycle.UNBINDED -> {
                parent.lifecycleObservable
                    .skip(1)
                    .startWith(Lifecycle.CREATED)
                    .subscribe(lifecycleConsumer)
            }

            null,
            Lifecycle.CREATED -> {
                parent.lifecycleObservable
                    .subscribe(lifecycleConsumer)
            }

            Lifecycle.DESTROYED -> {
                throw IllegalStateException("Presentation model can't be attached as a child to the already destroyed parent.")
            }

        }.untilDestroy()

        navigationMessages.observable
            .subscribe(parent.navigationMessages.consumer)
            .untilDestroy()
    }

    /**
     * Detaches this presentation model from parent.
     * @see [attachToParent]
     * @since 1.1.2
     */
    fun detachFromParent() {

        when (lifecycle.value) {

            Lifecycle.CREATED,
            Lifecycle.UNBINDED -> {
                lifecycleConsumer.accept(Lifecycle.DESTROYED)
            }

            Lifecycle.BINDED -> {
                lifecycleConsumer.accept(Lifecycle.UNBINDED)
                lifecycleConsumer.accept(Lifecycle.DESTROYED)
            }

            null,
            Lifecycle.DESTROYED -> {
                //  do nothing
            }
        }
    }

    /**
     * Local extension to add this [Disposable] to the [CompositeDisposable][compositeUnbind]
     * that will be CLEARED ON [UNBIND][Lifecycle.UNBINDED].
     */
    protected fun Disposable.untilUnbind() {
        compositeUnbind.add(this)
    }

    /**
     * Local extension to add this [Disposable] to the [CompositeDisposable][compositeDestroy]
     * that will be CLEARED ON [DESTROY][Lifecycle.DESTROYED].
     */
    protected fun Disposable.untilDestroy() {
        compositeDestroy.add(this)
    }

    /**
     * Returns the [Observable] that emits items when active, and buffers them when [unbinded][Lifecycle.UNBINDED].
     * Buffered items is emitted when this presentation model binds to the [view][PmView].
     * @param bufferSize number of items the buffer can hold. `null` means not constrained.
     */
    protected fun <T> Observable<T>.bufferWhileUnbind(bufferSize: Int? = null): Observable<T> {
        return this.bufferWhileIdle(unbind, bufferSize)
    }

    /**
     * Consumer of the [State].
     * Accessible only from a [PresentationModel].
     *
     * Use to subscribe the state to some [Observable] source.
     */
    protected val <T> State<T>.consumer: Consumer<T> get() = relay

    /**
     * Observable of the [Action].
     * Accessible only from a [PresentationModel].
     *
     * Use to subscribe to this [Action]s source.
     */
    protected val <T> Action<T>.observable: Observable<T> get() = relay

    /**
     * Consumer of the [Command].
     * Accessible only from a [PresentationModel].
     *
     * Use to subscribe the command to some [Observable] source.
     */
    protected val <T> Command<T>.consumer: Consumer<T> get() = relay

    /**
     * Reactive property for the [view's][PmView] state.
     * Can be observed and changed in reactive manner with it's [observable] and [consumer].
     *
     * Use to represent a view state. It can be something simple, like some widget's text, or complex,
     * like inProgress or data.
     *
     * @see Action
     * @see Command
     */
    inner class State<T>(initialValue: T? = null) {

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
            relay.subscribe { cachedValue.set(it) }
        }

        /**
         * Returns true if the [State] has any value.
         */
        fun hasValue() = cachedValue.get() != null
    }

    /**
     * Reactive property for the actions from the [view][PmView].
     * Can be changed and observed in reactive manner with it's [consumer] and [observable].
     *
     * Use to send actions of the view, e.g. some widget's clicks.
     *
     * @see State
     * @see Command
     */
    inner class Action<T> {
        internal val relay = PublishRelay.create<T>().toSerialized()

        /**
         * Consumer of the [Action][Action].
         */
        val consumer get() = relay.asConsumer()
    }

    /**
     * Reactive property for the commands to the [view][PmView].
     * Can be observed and changed in reactive manner with it's [observable] and [consumer].
     *
     * Use to represent a command to the view, e.g. toast or dialog showing.
     *
     * @param isIdle observable, that shows when `command` need to buffer the values (while isIdle value is true).
     * Buffered values will be delivered later (when isIdle emits false).
     * By default (when null is passed) it will buffer while the [view][PmView] is unbind from the [PresentationModel].
     *
     * @param bufferSize how many values should be kept in buffer. Null means no restrictions.
     *
     * @see Action
     * @see Command
     */
    inner class Command<T>(
        isIdle: Observable<Boolean>? = null,
        bufferSize: Int? = null
    ) {
        internal val relay = PublishRelay.create<T>().toSerialized()

        /**
         * Observable of this [Command].
         */
        val observable: Observable<T> =
            if (bufferSize == 0) {
                relay.asObservable()
            } else {
                if (isIdle == null) {
                    relay.bufferWhileUnbind(bufferSize)
                } else {
                    relay.bufferWhileIdle(isIdle, bufferSize)
                }
            }
                .publish()
                .apply { connect() }
    }

}

