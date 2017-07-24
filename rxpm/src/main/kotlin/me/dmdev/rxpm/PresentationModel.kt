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

    private val lifeCycleState = BehaviorRelay.createDefault<LifeCycleState>(LifeCycleState.NULL)
    private val unbindState = BehaviorRelay.create<Boolean>()

    val lifeCycleObservable = lifeCycleState.asObservable()
    val lifeCycleConsumer = lifeCycleState.asConsumer()

    init {
        lifeCycleState
                .takeUntil { it == LifeCycleState.ON_DESTROY }
                .subscribe {
                    when (it) {
                        LifeCycleState.ON_CREATE -> onCreate()
                        LifeCycleState.ON_BIND -> onBind()
                        LifeCycleState.ON_UNBIND -> {
                            compositeUnbind.clear()
                            onUnbind()
                        }
                        LifeCycleState.ON_DESTROY -> {
                            compositeDestroy.clear()
                            onDestroy()
                        }
                        LifeCycleState.NULL -> {
                            // do nothing ON_NULL
                        }
                    }
                }

        lifeCycleState
                .map {
                    when (it) {

                        LifeCycleState.ON_UNBIND,
                        LifeCycleState.ON_CREATE -> true

                        LifeCycleState.ON_BIND -> false

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
        this@PresentationModel.lifeCycleState
                .subscribe(this.lifeCycleConsumer)
                .untilDestroy()
    }

    protected fun <T> PublishRelay<T>.bufferWhileUnbind(bufferSize: Int? = null): Observable<T> {
        return this.bufferWhileIdle(unbindState, bufferSize)
    }

    enum class LifeCycleState {
        NULL, ON_CREATE, ON_DESTROY, ON_BIND, ON_UNBIND
    }
}
