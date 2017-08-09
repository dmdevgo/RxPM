package me.dmdev.rxpm

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/**
 * @author Dmitriy Gorbunov
 */
abstract class PresentationModel {

    private val compositeDestroy = CompositeDisposable()
    private val compositeUnbind = CompositeDisposable()

    private val lifeсycle = BehaviorRelay.create<Lifecycle>()
    private val unbind = BehaviorRelay.create<Boolean>()

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
                .map {
                    when (it) {
                        Lifecycle.BINDED -> false
                        else -> true
                    }
                }
                .subscribe(unbind)
                .untilDestroy()
    }

    protected open fun onCreate() {}

    protected open fun onBind() {}

    protected open fun onUnbind() {}

    protected open fun onDestroy() {}

    protected fun Disposable.untilUnbind() {
        compositeUnbind.add(this)
    }

    protected fun Disposable.untilDestroy() {
        compositeDestroy.add(this)
    }

    protected fun PresentationModel.bindLifecycle() {
        this@PresentationModel.lifeсycle
                .subscribe(this.lifecycleConsumer)
                .untilDestroy()
    }

    protected fun <T> Observable<T>.bufferWhileUnbind(bufferSize: Int? = null): Observable<T> {
        return this.bufferWhileIdle(unbind, bufferSize)
    }

    enum class Lifecycle {
        CREATED, BINDED, UNBINDED, DESTROYED
    }

    protected val <T> State<T>.consumer: Consumer<T> get() = relay
    protected val <T> Action<T>.observable: Observable<T> get() = relay
    protected val <T> Command<T>.consumer: Consumer<T> get() = relay

    class State<T>(initialValue: T? = null) {
        internal val relay =
                if (initialValue != null) BehaviorRelay.createDefault<T>(initialValue)
                else BehaviorRelay.create<T>()

        val observable = relay.asObservable()
        val value: T? get() = relay.value
        fun hasValue() = relay.hasValue()
    }

    class Action<T> {
        internal val relay = PublishRelay.create<T>()
        val consumer: Consumer<T> = relay
    }

    inner class Command<T>(isIdle: Observable<Boolean>? = null,
                           bufferSize: Int? = null) {
        internal val relay = PublishRelay.create<T>()
        val observable =
                if (isIdle == null) relay.bufferWhileUnbind(bufferSize)
                else relay.bufferWhileIdle(isIdle, bufferSize)
    }

}

