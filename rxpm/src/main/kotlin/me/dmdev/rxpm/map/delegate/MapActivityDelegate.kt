package me.dmdev.rxpm.map.delegate

import android.os.Bundle
import android.view.View
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.map.MapPmView
import me.dmdev.rxpm.map.MapPmExtension

class MapActivityDelegate<PM>(mapPmView: MapPmView<PM>) : BaseMapDelegate<PM>(mapPmView)
where PM : PresentationModel, PM : MapPmExtension {

    fun onPostCreate(contentView: View, savedInstanceState: Bundle?) {
        onCreateMapView(contentView, savedInstanceState)
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

    fun onDestroy() {
        onDestroyMapView()
    }
}