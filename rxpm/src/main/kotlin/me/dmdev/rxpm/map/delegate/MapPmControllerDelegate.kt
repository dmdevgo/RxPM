package me.dmdev.rxpm.map.delegate

import android.os.Bundle
import android.view.View
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.map.MapPmView
import me.dmdev.rxpm.map.MapPresentationModel

/**
 * @author Dmitriy Gorbunov
 */
class MapPmControllerDelegate<PM>(mapPmView: MapPmView<PM>) : BaseMapDelegate<PM>(mapPmView)
where PM : PresentationModel, PM : MapPresentationModel {

    fun onInitView(view: View, savedViewState: Bundle?) {
        onCreateMapView(view, savedViewState)
        mapView?.onStart()
    }

    fun onAttach() {
        mapView?.onResume()
    }

    fun onDetach() {
        mapView?.onPause()
    }

    fun onSaveViewState(outState: Bundle) {
        saveInstanceState(outState)
    }

    fun onDestroyView() {
        mapView?.onStop()
        onDestroyMapView()
    }
}