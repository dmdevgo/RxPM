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

import com.jakewharton.rxrelay2.*
import io.reactivex.*
import io.reactivex.functions.*

/**
 * Reactive property for the commands to the [view][PmView].
 * Can be observed and changed in reactive manner with it's [observable] and [PresentationModel.consumer].
 *
 * Use to represent a command to the view, e.g. toast or dialog showing.
 *
 * @param isIdle observable, that shows when `command` need to buffer the values (while isIdle value is true).
 * Buffered values will be delivered later (when isIdle emits false).
 * By default (when null is passed) it will buffer while the [view][PmView] is paused.
 *
 * @param bufferSize how many values should be kept in buffer. Null means no restrictions.
 *
 * @see Action
 * @see Command
 */
class Command<T> internal constructor(
    internal val pm: PresentationModel,
    isIdle: Observable<Boolean>? = null,
    bufferSize: Int? = null
) {
    internal val relay = PublishRelay.create<T>().toSerialized()

    /**
     * Observable of this [Command].
     */
    val observable: Observable<T> =
        if (bufferSize == 0) {
            relay.asObservable()
        } else {
            if (isIdle == null) {
                relay.bufferWhileIdle(pm.paused, bufferSize)
            } else {
                relay.bufferWhileIdle(isIdle, bufferSize)
            }
        }
            .publish()
            .apply { connect() }
}

/**
 * Creates the [Command].
 *
 * @param isIdle observable, that shows when `command` need to buffer the values (while isIdle value is true).
 * Buffered values will be delivered later (when isIdle emits false).
 * By default (when null is passed) it will buffer while the [view][PmView] is unbind from the [PresentationModel].
 *
 * @param bufferSize how many values should be kept in buffer. Null means no restrictions.
 */
fun <T> PresentationModel.command(
    isIdle: Observable<Boolean>? = null,
    bufferSize: Int? = null): Command<T> {

    return Command(this, isIdle, bufferSize)
}

/**
 * Subscribes to the [Command][Command] and adds it to the subscriptions list
 * that will be CLEARED ON [UNBIND][PresentationModel.Lifecycle.UNBINDED],
 * so use it ONLY in [PmView.onBindPresentationModel].
 */
infix fun <T> Command<T>.bindTo(consumer: Consumer<in T>) {
    with(pm) {
        this@bindTo.observable
            .subscribe(consumer)
            .untilUnbind()
    }
}

/**
 * Subscribe to the [Command][Command] and adds it to the subscriptions list
 * that will be CLEARED ON [UNBIND][PresentationModel.Lifecycle.UNBINDED],
 * so use it ONLY in [PmView.onBindPresentationModel].
 */
infix fun <T> Command<T>.bindTo(consumer: (T) -> Unit) {
    with(pm) {
        this@bindTo.observable
            .subscribe(consumer)
            .untilUnbind()
    }
}