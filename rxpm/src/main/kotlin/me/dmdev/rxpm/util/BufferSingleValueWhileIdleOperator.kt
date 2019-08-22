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

        private val compositeDisposable = CompositeDisposable()
        private var done = false

        private var isIdle = false
        private var bufferedValue: T? = null

        override fun onSubscribe(disposable: Disposable) {

            compositeDisposable.addAll(
                disposable,
                idleObserver.subscribe {
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
            )

            downstream.onSubscribe(compositeDisposable)
        }

        override fun onNext(v: T) {
            if (done) {
                return
            }

            if (isIdle) {
                bufferedValue = v
            } else {
                downstream.onNext(v)
            }
        }

        override fun onError(e: Throwable) {
            if (done) {
                RxJavaPlugins.onError(e)
                return
            }
            done = true
            compositeDisposable.dispose()
            downstream.onError(e)
        }

        override fun onComplete() {
            if (done) {
                return
            }

            done = true
            compositeDisposable.dispose()
            downstream.onComplete()
        }
    }
}