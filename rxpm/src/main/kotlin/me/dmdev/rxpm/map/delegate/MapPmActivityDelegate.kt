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
    private lateinit var mapPmDelegate: MapPmDelegate<PM>

    val presentationModel get() = pmDelegate.presentationModel

    fun onCreate(savedInstanceState: Bundle?) {
        pmDelegate.onCreate(savedInstanceState)
        mapPmDelegate = MapPmDelegate(pmDelegate.presentationModel, mapPmView, pmDelegate.pmBinder)
    }

    fun onPostCreate(contentView: View, savedInstanceState: Bundle?) {
        mapPmDelegate.onCreateMapView(contentView, savedInstanceState)
    }

    fun onStart() {
        pmDelegate.onStart()
        mapPmDelegate.onStart()
    }

    fun onResume() {
        pmDelegate.onResume()
        mapPmDelegate.onResume()
    }

    fun onPause() {
        pmDelegate.onPause()
        mapPmDelegate.onPause()
    }

    fun onSaveInstanceState(outState: Bundle) {
        pmDelegate.onSaveInstanceState(outState)
        mapPmDelegate.onSaveInstanceState(outState)
    }

    fun onStop() {
        pmDelegate.onStop()
        mapPmDelegate.onStop()
    }

    fun onDestroy() {
        pmDelegate.onDestroy()
        mapPmDelegate.onDestroyMapView()
    }

    fun onLowMemory() {
        mapPmDelegate.onLowMemory()
    }
}