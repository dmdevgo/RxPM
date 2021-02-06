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
import com.google.android.gms.maps.MapView
import me.dmdev.rxpm.AndroidPmView
import me.dmdev.rxpm.PresentationModel

/**
 * Interface that need to be implemented by the View that contains a [MapView].
 */
interface MapPmView<PM> : AndroidPmView<PM> where PM : PresentationModel, PM : MapPmExtension {

    /**
     *  [MapView] contained by this view.
     */
    var mapView: MapView?

    /**
     *  [GoogleMap] used by view.
     */
    var googleMap: GoogleMap?

    /**
     * Bind a [GoogleMap] to the [Presentation Model][presentationModel] in that method.
     */
    fun onBindMapPresentationModel(pm: PM, googleMap: GoogleMap)

}