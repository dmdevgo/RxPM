@file:Suppress("NOTHING_TO_INLINE")

package me.dmdev.rxpm

import com.jakewharton.rxrelay2.Relay
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function


/**
 * Convenience to get this [Relay] as an [Observable].
 * Helps to ensure code readability.
 */
inline fun <T> Relay<T>.asObservable(): Observable<T> {
    return this.hide()
}

/**
 * Convenience to get this [Relay] as an [Consumer].
 * Helps to ensure code readability.
 */
inline fun <T> Relay<T>.asConsumer(): Consumer<T> {
    return this
}

/**
 * Convenience to bind the [progress][progressConsumer] to the [Single].
 */
inline fun <T> Single<T>.bindProgress(progressConsumer: Consumer<Boolean>): Single<T> {
    return this
        .doOnSubscribe { progressConsumer.accept(true) }
        .doFinally { progressConsumer.accept(false) }
}

/**
 * Convenience to bind the [progress][progressConsumer] to the [Maybe].
 */
inline fun <T> Maybe<T>.bindProgress(progressConsumer: Consumer<Boolean>): Maybe<T> {
    return this
        .doOnSubscribe { progressConsumer.accept(true) }
        .doFinally { progressConsumer.accept(false) }
}

/**
 * Convenience to bind the [progress][progressConsumer] to the [Completable].
 */
inline fun Completable.bindProgress(progressConsumer: Consumer<Boolean>): Completable {
    return this
        .doOnSubscribe { progressConsumer.accept(true) }
        .doFinally { progressConsumer.accept(false) }
}

/**
 * Convenience to filter out items emitted by the source [Observable] when in progress ([progressState] last value is `true`).
 */
inline fun <T> Observable<T>.skipWhileInProgress(progressState: Observable<Boolean>): Observable<T> {
    return this
        .withLatestFrom(
            progressState.startWith(false),
            BiFunction { t: T, inProgress: Boolean ->
                Pair(t, inProgress)
            }
        )
        .filter { !it.second }
        .map { it.first }
}

/**
 * Returns the [Observable] that emits items when active, and buffers them when [idle][isIdle].
 * Buffered items is emitted when idle state ends.
 * @param isIdle shows when the idle state begins (`true`) and ends (`false`).
 * @param bufferSize number of items the buffer can hold. `null` means not constrained.
 */
inline fun <T> Observable<T>.bufferWhileIdle(
    isIdle: Observable<Boolean>,
    bufferSize: Int? = null
): Observable<T> {

    val itemsObservable =
        this
            .withLatestFrom(
                isIdle,
                BiFunction { t: T, idle: Boolean ->
                    Pair(t, idle)
                }
            )
            .publish()
            .autoConnect(2)

    return Observable
        .merge(
            itemsObservable
                .filter { it.second.not() } // isIdle = false
                .map { it.first }, // item

            itemsObservable
                .filter { it.second } // isIdle = true
                .map { it.first } // item
                .buffer(
                    isIdle.distinctUntilChanged().filter { it },
                    Function<Boolean, Observable<Boolean>> {
                        isIdle.distinctUntilChanged().filter { !it }
                    }
                )
                .map {
                    if (bufferSize != null) it.takeLast(bufferSize) else it
                }
                .flatMapIterable { it }

        )
        .publish()
        .apply { connect() }
}
