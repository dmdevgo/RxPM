package me.dmdev.rxpm.map.delegate

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import me.dmdev.rxpm.map.MapPmView
import me.dmdev.rxpm.map.MapPresentationModel

/**
 * @author Dmitriy Gorbunov
 */
class MapPmControllerDelegate<out PM : MapPresentationModel>(private val mapPmView: MapPmView<PM>) {

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

    fun onInitView(view: View, savedViewState: Bundle?) {
        mapView = findMapView(view) ?: throw IllegalArgumentException("MapView not found")
        mapView?.onCreate(savedViewState)
        mapView?.getMapAsync {
            googleMap = it
            mapPmView.onBindMapPresentationModel(mapPmView.pm, it)
            mapPmView.pm.mapReadyConsumer.accept(true)
        }
    }

    fun onAttach() {
        mapView?.onResume()
    }

    fun onDetach() {
        mapView?.onPause()
    }

    fun onSaveViewState(outState: Bundle) {
        mapView?.onSaveInstanceState(outState)
    }

    fun onDestroyView() {
        mapPmView.pm.mapReadyConsumer.accept(false)
        mapPmView.onUnbindMapPresentationModel()
        mapView?.onDestroy()
        mapView = null
        googleMap = null
    }

    fun onLowMemory() {
        mapView?.onLowMemory()
    }

    private fun findMapView(view: View): MapView? {
        if (view is MapView) {
            return view
        } else if (view is ViewGroup) {
            (0..view.childCount - 1)
                    .map { findMapView(view.getChildAt(it)) }
                    .filterIsInstance<MapView>()
                    .forEach { return it }
        }
        return null
    }
}