/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2021 Dmitriy Gorbunov (dmitriy.goto@gmail.com)
 *                     and Vasili Chyrvon (vasili.chyrvon@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.dmdev.rxpm

import com.jakewharton.rxrelay2.Relay
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import me.dmdev.rxpm.util.BufferSingleValueWhileIdleOperator
import me.dmdev.rxpm.util.BufferWhileIdleOperator


/**
 * Convenience to get this [Relay] as an [Observable].
 * Helps to ensure code readability.
 */
fun <T> Relay<T>.asObservable(): Observable<T> {
    return this.hide()
}

/**
 * Convenience to get this [Relay] as an [Consumer].
 * Helps to ensure code readability.
 */
fun <T> Relay<T>.asConsumer(): Consumer<T> {
    return this
}

/**
 * Convenience to bind the [progress][progressConsumer] to the [Single].
 */
fun <T> Single<T>.bindProgress(progressConsumer: Consumer<Boolean>): Single<T> {
    return this
        .doOnSubscribe { progressConsumer.accept(true) }
        .doFinally { progressConsumer.accept(false) }
}

/**
 * Convenience to bind the [progress][progressConsumer] to the [Maybe].
 */
fun <T> Maybe<T>.bindProgress(progressConsumer: Consumer<Boolean>): Maybe<T> {
    return this
        .doOnSubscribe { progressConsumer.accept(true) }
        .doFinally { progressConsumer.accept(false) }
}

/**
 * Convenience to bind the [progress][progressConsumer] to the [Completable].
 */
fun Completable.bindProgress(progressConsumer: Consumer<Boolean>): Completable {
    return this
        .doOnSubscribe { progressConsumer.accept(true) }
        .doFinally { progressConsumer.accept(false) }
}

/**
 * Convenience to bind the [progress][state] to the [Single].
 */
fun <T> Single<T>.bindProgress(state: State<Boolean>): Single<T> {
    return this.bindProgress(state.relay)
}

/**
 * Convenience to bind the [progress][state] to the [Maybe].
 */
fun <T> Maybe<T>.bindProgress(state: State<Boolean>): Maybe<T> {
    return this.bindProgress(state.relay)
}

/**
 * Convenience to bind the [progress][state] to the [Completable].
 */
fun Completable.bindProgress(state: State<Boolean>): Completable {
    return this.bindProgress(state.relay)
}

/**
 * Convenience to filter out items emitted by the source [Observable] when in progress ([progressState] last value is `true`).
 */
fun <T> Observable<T>.skipWhileInProgress(progressState: Observable<Boolean>): Observable<T> {
    return this
        .withLatestFrom(
            progressState.startWith(false),
            BiFunction { t: T, inProgress: Boolean ->
                Pair(t, inProgress)
            }
        )
        .filter { (_, inProgress) -> !inProgress }
        .map { (item, _) -> item }
}

/**
 * Convenience to filter out items emitted by the source [Observable] when in progress ([state] last value is `true`).
 */
fun <T> Observable<T>.skipWhileInProgress(state: State<Boolean>): Observable<T> {
    return this.skipWhileInProgress(state.observable)
}

/**
 * Returns the [Observable] that emits items when active, and buffers them when [idle][isIdle].
 * Buffered items is emitted when idle state ends.
 * @param isIdle shows when the idle state begins (`true`) and ends (`false`).
 * @param bufferSize number of items the buffer can hold. `null` means not constrained.
 */
internal fun <T> Observable<T>.bufferWhileIdle(
    isIdle: Observable<Boolean>,
    bufferSize: Int? = null
): Observable<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
        .lift(
            if (bufferSize == 1) {
                BufferSingleValueWhileIdleOperator(isIdle)
            } else {
                BufferWhileIdleOperator(isIdle, bufferSize)
            }
        )
}
