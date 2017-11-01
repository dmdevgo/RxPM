package me.dmdev.rxpm.map.delegate

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.delegate.PmSupportFragmentDelegate
import me.dmdev.rxpm.map.MapPmExtension
import me.dmdev.rxpm.map.MapPmView
import me.dmdev.rxpm.map.base.MapPmSupportFragment

/**
 * Delegate for the [Fragment] that helps with creation and binding of
 * a [presentation model][PresentationModel] and a [MapPmView].
 *
 * Use this class only if you can't subclass the [MapPmSupportFragment].
 *
 * Users of this class must forward all the life cycle methods from the containing Fragment
 * to the corresponding ones in this class.
 */
class MapPmSupportFragmentDelegate<PM, F>(private val mapPmView: F)
where PM : PresentationModel, PM : MapPmExtension, F: Fragment, F : MapPmView<PM> {

    private val pmDelegate = PmSupportFragmentDelegate(mapPmView)
    private val mapPmViewDelegate by lazy(LazyThreadSafetyMode.NONE) {
        MapPmViewDelegate(pmDelegate.presentationModel,
                          mapPmView,
                          pmDelegate.pmBinder)
    }

    val presentationModel get() = pmDelegate.presentationModel

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onCreate(savedInstanceState: Bundle?) {
        pmDelegate.onCreate(savedInstanceState)
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onCreateView(view: View, savedInstanceState: Bundle?) {
        mapPmViewDelegate.onCreateMapView(view, savedInstanceState)
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onStart() {
        pmDelegate.onStart()
        mapPmViewDelegate.onStart()
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onResume() {
        pmDelegate.onResume()
        mapPmViewDelegate.onResume()
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onPause() {
        pmDelegate.onPause()
        mapPmViewDelegate.onPause()
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onSaveInstanceState(outState: Bundle) {
        pmDelegate.onSaveInstanceState(outState)
        mapPmViewDelegate.onSaveInstanceState(outState)
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onStop() {
        pmDelegate.onStop()
        mapPmViewDelegate.onStop()
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onDestroyView() {
        mapPmViewDelegate.onDestroyMapView()
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onDestroy() {
        pmDelegate.onDestroy()
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onLowMemory() {
        mapPmViewDelegate.onLowMemory()
    }
}
