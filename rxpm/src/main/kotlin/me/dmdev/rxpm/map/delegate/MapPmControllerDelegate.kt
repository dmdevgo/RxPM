package me.dmdev.rxpm.map.delegate

import android.os.Bundle
import android.view.View
import com.bluelinelabs.conductor.Controller
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.delegate.PmControllerDelegate
import me.dmdev.rxpm.map.MapPmExtension
import me.dmdev.rxpm.map.MapPmView
import me.dmdev.rxpm.map.base.MapPmController

/**
 * Delegate for the [Controller] that helps with creation and binding of
 * a [presentation model][PresentationModel] and a [MapPmView].
 *
 * Use this class only if you can't subclass the [MapPmController].
 *
 * Users of this class must forward all the life cycle methods from the containing Controller
 * to the corresponding ones in this class.
 */
class MapPmControllerDelegate<PM, C>(private val mapPmView: C)
where PM : PresentationModel, PM : MapPmExtension, C : Controller, C : MapPmView<PM> {

    private val pmDelegate = PmControllerDelegate(mapPmView)
    private val mapPmViewDelegate by lazy {
        MapPmViewDelegate(pmDelegate.presentationModel,
                          mapPmView,
                          pmDelegate.pmBinder)
    }

    val presentationModel get() = pmDelegate.presentationModel

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onCreateView(view: View, savedViewState: Bundle?) {
        pmDelegate.onCreateView()
        mapPmViewDelegate.onCreateMapView(view, savedViewState)
        mapPmViewDelegate.onStart()
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onAttach() {
        pmDelegate.onAttach()
        mapPmViewDelegate.onResume()
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onDetach() {
        pmDelegate.onDetach()
        mapPmViewDelegate.onPause()
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onSaveViewState(outState: Bundle) {
        mapPmViewDelegate.onSaveInstanceState(outState)
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onDestroyView() {
        pmDelegate.onDestroyView()
        mapPmViewDelegate.onStop()
        mapPmViewDelegate.onDestroyMapView()
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onDestroy() {
        pmDelegate.onDestroy()
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onLowMemory() {
        mapPmViewDelegate.onLowMemory()
    }
}