package me.dmdev.rxpm

import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.functions.Consumer

/**
 * @author Dmitriy Gorbunov
 */

inline fun<T> Relay<T>.asObservable(): Observable<T> {
    return this.hide()
}

inline fun<T> Relay<T>.asConsumer(): Consumer<T> {
    return this
}
