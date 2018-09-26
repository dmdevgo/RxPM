package me.dmdev.rxpm.map.delegate

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.delegate.PmFragmentDelegate
import me.dmdev.rxpm.map.MapPmExtension
import me.dmdev.rxpm.map.MapPmView
import me.dmdev.rxpm.map.base.MapPmFragment

/**
 * Delegate for the [Fragment] that helps with creation and binding of
 * a [presentation model][PresentationModel] and a [MapPmView].
 *
 * Use this class only if you can't subclass the [MapPmFragment].
 *
 * Users of this class must forward all the life cycle methods from the containing Fragment
 * to the corresponding ones in this class.
 */
class MapPmFragmentDelegate<PM, F>(private val mapPmFragment: F)
        where PM : PresentationModel, PM : MapPmExtension,
              F : Fragment, F : MapPmView<PM> {

    private val pmDelegate = PmFragmentDelegate(mapPmFragment)
    private val mapPmViewDelegate by lazy(LazyThreadSafetyMode.NONE) {
        MapPmViewDelegate(pmDelegate.presentationModel, mapPmFragment, pmDelegate.pmBinder)
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
