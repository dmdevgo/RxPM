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

package me.dmdev.rxpm.map

import com.google.android.gms.maps.GoogleMap
import com.jakewharton.rxrelay2.BehaviorRelay
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.asConsumer
import me.dmdev.rxpm.asObservable

/**
 * This interface contains additions for [PresentationModel] that binds to the [MapPmView].
 *
 * You also can subclass [MapPresentationModel] that already implemented this.
 */
interface MapPmExtension {

    /**
     * The state of [map][GoogleMap] readiness.
     */
    val mapReadyState: MapReadyState

    /**
     * Returns a [command][PresentationModel.Command] that will buffer the values
     * until the [view][MapPmView] will be bound to the [map][GoogleMap].
     *
     * Use to represent a command to the [MapPmView], e.g. pin moves or zoom changes.
     *
     * @param bufferSize how many values should be kept in buffer. Null means no restrictions.
     */
    fun <T> PresentationModel.mapCommand(bufferSize: Int? = null): PresentationModel.Command<T> {
        return Command(mapReadyState.observable.map { !it }, bufferSize)
    }

    /**
     * This class represents the state of [map][GoogleMap] readiness.
     */
    class MapReadyState {
        private val ready = BehaviorRelay.createDefault(false)
        internal val consumer get() = ready.asConsumer()

        /**
         * Observable of the [map][GoogleMap] readiness state.
         */
        val observable get() = ready.asObservable()
    }
}