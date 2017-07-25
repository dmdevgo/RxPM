package me.dmdev.rxpm

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * @author Dmitriy Gorbunov
 */
abstract class PresentationModel {

    private val compositeDestroy = CompositeDisposable()
    private val compositeUnbind = CompositeDisposable()

    private val lifeсycleState = BehaviorRelay.createDefault<Lifecycle>(Lifecycle.NULL)
    private val unbindState = BehaviorRelay.create<Boolean>()

    val lifeCycleObservable = lifeсycleState.asObservable()
    val lifeCycleConsumer = lifeсycleState.asConsumer()

    init {
        lifeсycleState
                .takeUntil { it == Lifecycle.ON_DESTROY }
                .subscribe {
                    when (it) {
                        Lifecycle.ON_CREATE -> onCreate()
                        Lifecycle.ON_BIND -> onBind()
                        Lifecycle.ON_UNBIND -> {
                            compositeUnbind.clear()
                            onUnbind()
                        }
                        Lifecycle.ON_DESTROY -> {
                            compositeDestroy.clear()
                            onDestroy()
                        }
                        Lifecycle.NULL -> {
                            // do nothing ON_NULL
                        }
                    }
                }

        lifeсycleState
                .map {
                    when (it) {

                        Lifecycle.ON_UNBIND,
                        Lifecycle.ON_CREATE -> true

                        Lifecycle.ON_BIND -> false

                        else -> {
                            false
                        }
                    }
                }
                .subscribe(unbindState)
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
        this@PresentationModel.lifeсycleState
                .subscribe(this.lifeCycleConsumer)
                .untilDestroy()
    }

    protected fun <T> PublishRelay<T>.bufferWhileUnbind(bufferSize: Int? = null): Observable<T> {
        return this.bufferWhileIdle(unbindState, bufferSize)
    }

    enum class Lifecycle {
        NULL, ON_CREATE, ON_DESTROY, ON_BIND, ON_UNBIND
    }
}
