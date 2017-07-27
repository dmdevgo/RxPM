package me.dmdev.rxpm

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * @author Dmitriy Gorbunov
 */
abstract class PresentationModel {

    private val compositeDestroy = CompositeDisposable()
    private val compositeUnbind = CompositeDisposable()

    private val lifeсycle = BehaviorRelay.create<Lifecycle>()
    private val unbind = BehaviorRelay.create<Boolean>()

    val lifecycleState = lifeсycle.asObservable()
    val lifecycleConsumer = lifeсycle.asConsumer()

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
}
