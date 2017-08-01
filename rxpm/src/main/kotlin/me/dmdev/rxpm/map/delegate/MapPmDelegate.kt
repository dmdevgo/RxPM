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

/**
 * @author Dmitriy Gorbunov
 */
internal class MapPmDelegate<PM>(private val pm: PM,
                                 private val mapPmView: MapPmView<PM>,
                                 pmBinder: PmBinder<PM>)
where PM : PresentationModel, PM : MapPmExtension {

    companion object {
        private const val MAP_VIEW_BUNDLE_KEY = "map_view_bundle"
    }

    init {
        pmBinder.listener = object : PmBinder.Callbacks {
            override fun onBindPm() {
                // TODO
            }

            override fun onUnbindPm() {
                // TODO
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
            mapPmView.onBindMapPresentationModel(pm, it)
            pm.mapReadiness.consumer.accept(true)
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
        mapPmView.presentationModel.mapReadiness.consumer.accept(false)
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