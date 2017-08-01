package me.dmdev.rxpm.map.delegate

import android.os.Bundle
import android.view.View
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.delegate.PmControllerDelegate
import me.dmdev.rxpm.map.MapPmExtension
import me.dmdev.rxpm.map.MapPmView

/**
 * @author Dmitriy Gorbunov
 */
class MapPmControllerDelegate<out PM>(private val mapPmView: MapPmView<PM>)
where PM : PresentationModel, PM : MapPmExtension {

    private val pmDelegate = PmControllerDelegate(mapPmView)
    private lateinit var mapPmDelegate: MapPmDelegate<PM>

    val presentationModel get() = pmDelegate.presentationModel

    fun onCreate() {
        pmDelegate.onCreate()
        mapPmDelegate = MapPmDelegate(pmDelegate.presentationModel, mapPmView)
    }

    fun onCreateView(view: View, savedViewState: Bundle?) {
        mapPmDelegate.onCreateMapView(view, savedViewState)
        mapPmDelegate.onStart()
    }

    fun onAttach() {
        pmDelegate.onAttach()
        mapPmDelegate.onResume()
    }

    fun onDetach() {
        pmDelegate.onDetach()
        mapPmDelegate.onPause()
    }

    fun onSaveViewState(outState: Bundle) {
        mapPmDelegate.onSaveInstanceState(outState)
    }

    fun onDestroyView() {
        pmDelegate.onDestroyView()
        mapPmDelegate.onStop()
        mapPmDelegate.onDestroyMapView()
    }

    fun onDestroy() {
        pmDelegate.onDestroy()
    }

    fun onLowMemory() {
        mapPmDelegate.onLowMemory()
    }
}