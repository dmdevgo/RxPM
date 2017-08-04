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
    private val mapPmViewDelegate by lazy {
        MapPmViewDelegate(pmDelegate.presentationModel,
                          mapPmView,
                          pmDelegate.pmBinder)
    }

    val presentationModel get() = pmDelegate.presentationModel

    fun onCreate(savedInstanceState: Bundle?) {
        pmDelegate.onCreate(savedInstanceState)
    }

    fun onCreateView(view: View, savedInstanceState: Bundle?) {
        mapPmViewDelegate.onCreateMapView(view, savedInstanceState)
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

    fun onDestroyView() {
        mapPmViewDelegate.onDestroyMapView()
    }

    fun onDestroy() {
        pmDelegate.onDestroy()
    }

    fun onLowMemory() {
        mapPmViewDelegate.onLowMemory()
    }
}
