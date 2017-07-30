package me.dmdev.rxpm.map.base

import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.base.PmController
import me.dmdev.rxpm.map.MapPmView
import me.dmdev.rxpm.map.MapPresentationModel
import me.dmdev.rxpm.map.delegate.MapPmControllerDelegate

/**
 * @author Dmitriy Gorbunov
 */
abstract class MapController<PM>(args: Bundle? = null) : PmController<PM>(args),
                                                         MapPmView<PM>
where PM : PresentationModel, PM : MapPresentationModel {

    @Suppress("LeakingThis")
    private val mapDelegate = MapPmControllerDelegate(this)

    override var mapView: MapView? = null
    override var googleMap: GoogleMap? = null

    override fun onInitView(view: View, savedViewState: Bundle?) {
        super.onInitView(view, savedViewState)
        mapDelegate.onInitView(view, savedViewState)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        mapDelegate.onAttach()
    }

    override fun onDetach(view: View) {
        super.onDetach(view)
        mapDelegate.onDetach()
    }

    override fun onSaveViewState(view: View, outState: Bundle) {
        super.onSaveViewState(view, outState)
        mapDelegate.onSaveViewState(outState)
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        mapDelegate.onDestroyView()
    }

    // Call from outside
    fun onLowMemory() {
        mapDelegate.onLowMemory()
    }
}