package me.dmdev.rxpm

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import java.util.concurrent.atomic.AtomicReference

/**
 * Parent class for any Presentation Model.
 * @author Dmitriy Gorbunov
 */
abstract class PresentationModel {

    enum class Lifecycle {
        CREATED, BINDED, UNBINDED, DESTROYED
    }

    private val compositeDestroy = CompositeDisposable()
    private val compositeUnbind = CompositeDisposable()

    private val lifeсycle = BehaviorRelay.create<Lifecycle>()
    private val unbind = BehaviorRelay.createDefault<Boolean>(true)

    /**
     * The [lifecycle][Lifecycle] state of this presentation model.
     */
    val lifecycleState = lifeсycle.asObservable()
    internal val lifecycleConsumer = lifeсycle.asConsumer()

    init {
        lifeсycle
                .takeUntil { it == Lifecycle.DESTROYED }
                .subscribe {
                    when (it) {
                        Lifecycle.CREATED -> onCreate()
                        Lifecycle.BINDED -> onBind()
                        Lifecycle.UNBINDED -> {
                            compositeUnbind.clear()
                            onUnbind()
                        }
                        Lifecycle.DESTROYED -> {
                            compositeDestroy.clear()
                            onDestroy()
                        }
                    }
                }

        lifeсycle
                .takeUntil { it == Lifecycle.DESTROYED }
                .subscribe {
                    when (it) {
                        Lifecycle.BINDED -> unbind.accept(false)
                        Lifecycle.UNBINDED -> unbind.accept(true)
                        else -> {}
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
     * Binds `this` [PresentationModel]'s (child) lifecycle to the enclosing presentation model's (parent) lifecycle.
     */
    protected fun PresentationModel.bindLifecycle() {
        this@PresentationModel.lifeсycle
                .subscribe(this.lifecycleConsumer)
                .untilDestroy()
    }

    /**
     * Returns the [Observable] that emits items when active, and buffers them when [unbinded][Lifecycle.UNBINDED].
     * Buffered items is emitted when this presentation model binds to the [view][PmView].
     * @param bufferSize number of items the buffer can hold. `null` means not constrained.
     */
    protected fun <T> Observable<T>.bufferWhileUnbind(bufferSize: Int? = null): Observable<T> {
        return this.bufferWhileIdle(unbind, bufferSize)
    }

    // TODO: Add the javadocs fot the State, Command and Action.
    protected val <T> State<T>.consumer: Consumer<T> get() = relay
    protected val <T> Action<T>.observable: Observable<T> get() = relay
    protected val <T> Command<T>.consumer: Consumer<T> get() = relay

    inner class State<T>(initialValue: T? = null) {
        internal val relay =
                if (initialValue != null) BehaviorRelay.createDefault<T>(initialValue).toSerialized()
                else BehaviorRelay.create<T>().toSerialized()

        private val cachedValue =
                if (initialValue != null) AtomicReference<T?>(initialValue)
                else AtomicReference<T?>()

        val observable = relay.asObservable()
        val value: T
            get() {
                return cachedValue.get() ?: throw UninitializedPropertyAccessException(
                        "The State has no value yet. Use valueOrNull() or pass initialValue to the constructor.")
            }

        val valueOrNull: T? get() = cachedValue.get()

        init {
            relay.subscribe { cachedValue.set(it) }
        }

        fun hasValue() = cachedValue.get() != null
    }

    inner class Action<T> {
        internal val relay = PublishRelay.create<T>().toSerialized()
        val consumer = relay.asConsumer()
    }

    inner class Command<T>(isIdle: Observable<Boolean>? = null,
                           bufferSize: Int? = null) {
        internal val relay = PublishRelay.create<T>().toSerialized()
        val observable =
                if (isIdle == null) relay.bufferWhileUnbind(bufferSize)
                else relay.bufferWhileIdle(isIdle, bufferSize)
    }

}

