package me.dmdev.rxpm

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Consumer

/**
 * @author Dmitriy Gorbunov
 */

inline fun <T> Relay<T>.asObservable(): Observable<T> {
    return this.hide()
}

inline fun <T> Relay<T>.asConsumer(): Consumer<T> {
    return this
}

inline fun <T> Single<T>.bindProgress(progress: BehaviorRelay<Boolean>): Single<T> {
    return this
            .doOnSubscribe { progress.accept(true) }
            .doOnSuccess { progress.accept(false) }
            .doOnError { progress.accept(false) }
}

inline fun Completable.bindProgress(progress: BehaviorRelay<Boolean>): Completable {
    return this
            .doOnSubscribe { progress.accept(true) }
            .doOnTerminate { progress.accept(false) }
}

inline fun <T> Observable<T>.skipWhileProgress(progress: BehaviorRelay<Boolean>): Observable<T> {
    return this.filter { progress.value == false }
}
