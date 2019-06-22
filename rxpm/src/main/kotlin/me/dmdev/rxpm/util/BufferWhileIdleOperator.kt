package me.dmdev.rxpm.util

import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.*
import io.reactivex.plugins.*
import java.util.*

internal class BufferWhileIdleOperator<T>(
    private val idleObserver: Observable<Boolean>,
    private val bufferSize: Int? = null
) : ObservableOperator<T, T> {

    override fun apply(observer: Observer<in T>): Observer<in T> {
        return ObserverWithBuffer(idleObserver, observer, bufferSize)
    }

    class ObserverWithBuffer<T>(
        private val idleObserver: Observable<Boolean>,
        private val downstream: Observer<in T>,
        private val bufferSize: Int? = null
    ) : Observer<T> {

        private val compositeDisposable = CompositeDisposable()
        private var done = false

        private var isIdle = false
        private var bufferedValues: Queue<T> = LinkedList()

        override fun onSubscribe(disposable: Disposable) {
            compositeDisposable.addAll(
                disposable,
                idleObserver.subscribe {
                    if (it) {
                        isIdle = true
                    } else {
                        isIdle = false
                        bufferedValues.forEach { v ->
                            onNext(v)
                        }
                        bufferedValues.clear()
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

                if (bufferedValues.size == bufferSize) {
                    bufferedValues.poll()
                }

                bufferedValues.offer(v)

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