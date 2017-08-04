package me.dmdev.rxpm.map.delegate

import android.os.Bundle
import android.view.View
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.delegate.PmActivityDelegate
import me.dmdev.rxpm.map.MapPmExtension
import me.dmdev.rxpm.map.MapPmView

class MapPmActivityDelegate<out PM>(private val mapPmView: MapPmView<PM>)
where PM : PresentationModel, PM : MapPmExtension {

    private val pmDelegate = PmActivityDelegate(mapPmView)
    private val mapPmViewDelegate by lazy {
        MapPmViewDelegate(pmDelegate.presentationModel,
                          mapPmView,
                          pmDelegate.pmBinder)
    }

    val presentationModel get() = pmDelegate.presentationModel

    fun onCreate(savedInstanceState: Bundle?) {
        pmDelegate.onCreate(savedInstanceState)
    }

    fun onPostCreate(contentView: View, savedInstanceState: Bundle?) {
        mapPmViewDelegate.onCreateMapView(contentView, savedInstanceState)
    }

    fun onStart() {
        pmDelegate.onStart()
        mapPmViewDelegate.onStart()
    }

    fun onResume() {
        pmDelegate.onResume()
        mapPmViewDelegate.onResume()
    }

    fun onPause() {
        pmDelegate.onPause()
        mapPmViewDelegate.onPause()
    }

    fun onSaveInstanceState(outState: Bundle) {
        pmDelegate.onSaveInstanceState(outState)
        mapPmViewDelegate.onSaveInstanceState(outState)
    }

    fun onStop() {
        pmDelegate.onStop()
        mapPmViewDelegate.onStop()
    }

    fun onDestroy() {
        pmDelegate.onDestroy()
        mapPmViewDelegate.onDestroyMapView()
    }

    fun onLowMemory() {
        mapPmViewDelegate.onLowMemory()
    }
}