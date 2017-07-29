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
open class BaseMapDelegate<PM : MapPresentationModel>(private val mapPmView: MapPmView<PM>) {

    companion object {
        private const val MAP_VIEW_BUNDLE_KEY = "map_view_bundle"
    }

    protected var mapView: MapView?
        get() = mapPmView.mapView
        set(value) {
            mapPmView.mapView = value
        }

    protected var googleMap: GoogleMap?
        get() = mapPmView.googleMap
        set(value) {
            mapPmView.googleMap = value
        }

    fun onLowMemory() {
        mapView?.onLowMemory()
    }

    protected fun onCreateMapView(view: View, savedInstanceState: Bundle?) {
        mapView = findMapView(view) ?: throw IllegalArgumentException("MapView not found")
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        mapView?.onCreate(savedInstanceState?.getBundle(MAP_VIEW_BUNDLE_KEY))
        mapView?.getMapAsync {
            googleMap = it
            mapPmView.onBindMapPresentationModel(mapPmView.presentationModel, it)
            mapPmView.presentationModel.mapReadyConsumer.accept(true)
        }
    }

    protected fun onDestroyMapView() {
        mapPmView.presentationModel.mapReadyConsumer.accept(false)
        mapPmView.onUnbindMapPresentationModel()
        mapView?.onDestroy()
        mapView = null
        googleMap = null
    }

    protected fun saveInstanceState(outState: Bundle) {
        var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView?.onSaveInstanceState(mapViewBundle)
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