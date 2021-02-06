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
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.delegate.PmBinder
import me.dmdev.rxpm.map.MapPmExtension
import me.dmdev.rxpm.map.MapPmView


internal class MapPmViewDelegate<PM>(
    private val pm: PM,
    private val mapPmView: MapPmView<PM>,
    private val pmBinder: PmBinder<PM>
) where PM : PresentationModel, PM : MapPmExtension {

    companion object {
        private const val MAP_VIEW_BUNDLE_KEY = "map_view_bundle"
    }

    private var mapReady = false

    init {
        pmBinder.listener = object : PmBinder.Callbacks {
            override fun onBindPm() {
                tryBindMapViewToPm()
            }

            override fun onUnbindPm() {
                // do nothing
            }
        }
    }

    private var mapView: MapView?
        get() = mapPmView.mapView
        set(value) {
            mapPmView.mapView = value
        }

    private var googleMap: GoogleMap?
        get() = mapPmView.googleMap
        set(value) {
            mapPmView.googleMap = value
        }

    fun onCreateMapView(view: View, savedInstanceState: Bundle?) {
        mapView = findMapView(view) ?: throw IllegalArgumentException("MapView not found")
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        mapView?.onCreate(savedInstanceState?.getBundle(MAP_VIEW_BUNDLE_KEY))
        mapView?.getMapAsync {
            googleMap = it
            mapReady = true
            tryBindMapViewToPm()
        }
    }

    fun onStart() {
        mapView?.onStart()
    }

    fun onResume() {
        mapView?.onResume()
    }

    fun onPause() {
        mapView?.onPause()
    }

    fun onSaveInstanceState(outState: Bundle) {
        var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView?.onSaveInstanceState(mapViewBundle)
    }

    fun onStop() {
        mapView?.onStop()
    }

    fun onDestroyMapView() {
        if (mapReady) pm.mapReadyState.consumer.accept(false)
        mapView?.onDestroy()
        mapReady = false
        mapView = null
        googleMap = null
    }

    fun onLowMemory() {
        mapView?.onLowMemory()
    }

    private fun tryBindMapViewToPm() {
        if (mapReady && pmBinder.viewBound) {
            mapPmView.onBindMapPresentationModel(pm, googleMap!!)
            pm.mapReadyState.consumer.accept(true)
        }
    }

    private fun findMapView(view: View): MapView? {
        if (view is MapView) {
            return view
        } else if (view is ViewGroup) {
            (0 until view.childCount)
                .map { findMapView(view.getChildAt(it)) }
                .filterIsInstance<MapView>()
                .forEach { return it }
        }
        return null
    }
}