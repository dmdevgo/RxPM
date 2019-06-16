package me.dmdev.rxpm.util

import io.reactivex.*
import io.reactivex.disposables.*
import io.reactivex.plugins.*

internal class BufferSingleValueWhileIdleOperator<T>(
    private val idleObserver: Observable<Boolean>
) : ObservableOperator<T, T> {

    override fun apply(observer: Observer<in T>): Observer<in T> {
        return ObserverWithBuffer(idleObserver, observer)
    }

    class ObserverWithBuffer<T>(
        private val idleObserver: Observable<Boolean>,
        private val downstream: Observer<in T>
    ) : Observer<T> {

        private var idleObserverDisposable: Disposable? = null
        private var upstream: Disposable? = null
        private var done = false

        private var isIdle = false
        private var bufferedValue: T? = null

        override fun onSubscribe(d: Disposable) {
            upstream = d
            idleObserverDisposable = idleObserver.subscribe {
                if (it) {
                    isIdle = true
                } else {
                    isIdle = false
                    bufferedValue?.let { value ->
                        onNext(value)
                    }
                    bufferedValue = null
                }
            }
        }

        override fun onNext(t: T) {
            if (done) {
                return
            }

            if (isIdle) {
                bufferedValue = t
            } else {
                downstream.onNext(t)
            }
        }

        override fun onError(e: Throwable) {
            if (done) {
                RxJavaPlugins.onError(e)
                return
            }
            done = true
            downstream.onError(e)
            idleObserverDisposable?.dispose()
        }

        override fun onComplete() {
            if (done) {
                return
            }

            idleObserverDisposable?.dispose()
            done = true
            downstream.onComplete()
        }
    }
}