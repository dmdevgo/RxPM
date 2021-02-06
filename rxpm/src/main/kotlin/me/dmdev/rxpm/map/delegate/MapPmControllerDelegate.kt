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

import android.os.Bundle
import android.view.View
import com.bluelinelabs.conductor.Controller
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.delegate.PmControllerDelegate
import me.dmdev.rxpm.map.MapPmExtension
import me.dmdev.rxpm.map.MapPmView
import me.dmdev.rxpm.map.base.MapPmController

/**
 * Delegate for the [Controller] that helps with creation and binding of
 * a [presentation model][PresentationModel] and a [MapPmView].
 *
 * Use this class only if you can't subclass the [MapPmController].
 *
 * Users of this class must forward all the life cycle methods from the containing Controller
 * to the corresponding ones in this class.
 */
class MapPmControllerDelegate<PM, C>(private val mapPmView: C)
        where PM : PresentationModel, PM : MapPmExtension,
              C : Controller, C : MapPmView<PM> {

    private val pmDelegate = PmControllerDelegate(mapPmView)
    private val mapPmViewDelegate by lazy(LazyThreadSafetyMode.NONE) {
        MapPmViewDelegate(pmDelegate.presentationModel, mapPmView, pmDelegate.pmBinder)
    }

    val presentationModel get() = pmDelegate.presentationModel

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onCreateView(view: View, savedViewState: Bundle?) {
        pmDelegate.onCreateView()
        mapPmViewDelegate.onCreateMapView(view, savedViewState)
        mapPmViewDelegate.onStart()
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onAttach() {
        pmDelegate.onAttach()
        mapPmViewDelegate.onResume()
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onDetach() {
        pmDelegate.onDetach()
        mapPmViewDelegate.onPause()
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onSaveViewState(outState: Bundle) {
        mapPmViewDelegate.onSaveInstanceState(outState)
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onDestroyView() {
        pmDelegate.onDestroyView()
        mapPmViewDelegate.onStop()
        mapPmViewDelegate.onDestroyMapView()
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onDestroy() {
        pmDelegate.onDestroy()
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onLowMemory() {
        mapPmViewDelegate.onLowMemory()
    }
}