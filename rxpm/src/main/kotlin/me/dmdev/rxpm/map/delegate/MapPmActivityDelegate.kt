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

package me.dmdev.rxpm.map.delegate

import android.app.Activity
import android.os.Bundle
import android.view.View
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.delegate.PmActivityDelegate
import me.dmdev.rxpm.map.MapPmExtension
import me.dmdev.rxpm.map.MapPmView
import me.dmdev.rxpm.map.base.MapPmSupportActivity

/**
 * Delegate for the [Activity] that helps with creation and binding of
 * a [presentation model][PresentationModel] and a [MapPmView].
 *
 * Use this class only if you can't subclass the [MapPmSupportActivity].
 *
 * Users of this class must forward all the life cycle methods from the containing Activity
 * to the corresponding ones in this class.
 */
class MapPmActivityDelegate<PM, A>(private val mapPmView: A)
        where PM : PresentationModel, PM : MapPmExtension,
              A : Activity, A : MapPmView<PM> {

    private val pmDelegate = PmActivityDelegate(mapPmView)
    private val mapPmViewDelegate by lazy(LazyThreadSafetyMode.NONE) {
        MapPmViewDelegate(pmDelegate.presentationModel, mapPmView, pmDelegate.pmBinder)
    }

    val presentationModel get() = pmDelegate.presentationModel

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onCreate(savedInstanceState: Bundle?) {
        pmDelegate.onCreate(savedInstanceState)
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onPostCreate(contentView: View, savedInstanceState: Bundle?) {
        mapPmViewDelegate.onCreateMapView(contentView, savedInstanceState)
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onStart() {
        pmDelegate.onStart()
        mapPmViewDelegate.onStart()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onResume() {
        pmDelegate.onResume()
        mapPmViewDelegate.onResume()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onPause() {
        pmDelegate.onPause()
        mapPmViewDelegate.onPause()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onSaveInstanceState(outState: Bundle) {
        pmDelegate.onSaveInstanceState(outState)
        mapPmViewDelegate.onSaveInstanceState(outState)
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onStop() {
        pmDelegate.onStop()
        mapPmViewDelegate.onStop()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onDestroy() {
        pmDelegate.onDestroy()
        mapPmViewDelegate.onDestroyMapView()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onLowMemory() {
        mapPmViewDelegate.onLowMemory()
    }
}