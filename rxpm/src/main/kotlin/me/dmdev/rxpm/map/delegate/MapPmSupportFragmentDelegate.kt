package me.dmdev.rxpm.map.delegate

import android.os.Bundle
import android.view.View
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.delegate.PmSupportFragmentDelegate
import me.dmdev.rxpm.map.MapPmExtension
import me.dmdev.rxpm.map.MapPmView

/**
 * @author Dmitriy Gorbunov
 */
class MapPmSupportFragmentDelegate<out PM>(private val mapPmView: MapPmView<PM>)
where PM : PresentationModel, PM : MapPmExtension {

    private val pmDelegate = PmSupportFragmentDelegate(mapPmView)
    private lateinit var mapPmDelegate: MapPmDelegate<PM>

    val presentationModel get() = pmDelegate.presentationModel

    fun onCreate(savedInstanceState: Bundle?) {
        pmDelegate.onCreate(savedInstanceState)
        mapPmDelegate = MapPmDelegate(pmDelegate.presentationModel, mapPmView, pmDelegate.pmBinder)
    }

    fun onCreateView(view: View, savedInstanceState: Bundle?) {
        mapPmDelegate.onCreateMapView(view, savedInstanceState)
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

    fun onDestroyView() {
        mapPmDelegate.onDestroyMapView()
    }

    fun onDestroy() {
        pmDelegate.onDestroy()
    }

    fun onLowMemory() {
        mapPmDelegate.onLowMemory()
    }
}
