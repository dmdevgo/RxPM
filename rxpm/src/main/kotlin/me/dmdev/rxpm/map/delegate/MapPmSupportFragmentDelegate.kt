package me.dmdev.rxpm.map.delegate

import android.os.Bundle
import android.view.View
import me.dmdev.rxpm.map.MapPmView
import me.dmdev.rxpm.map.MapPresentationModel

/**
 * @author Dmitriy Gorbunov
 */
class MapPmSupportFragmentDelegate<PM : MapPresentationModel>(mapPmView: MapPmView<PM>)
    : BaseMapDelegate<PM>(mapPmView) {

    fun onCreateView(view: View, savedInstanceState: Bundle?) {
        onCreateMapView(view, savedInstanceState)
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
        saveInstanceState(outState)
    }

    fun onStop() {
        mapView?.onStop()
    }

    fun onDestroyView() {
        onDestroyMapView()
    }
}
