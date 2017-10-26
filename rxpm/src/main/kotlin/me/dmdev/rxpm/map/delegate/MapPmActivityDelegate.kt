package me.dmdev.rxpm.map.delegate

import android.app.Activity
import android.os.Bundle
import android.view.View
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.delegate.PmActivityDelegate
import me.dmdev.rxpm.map.MapPmExtension
import me.dmdev.rxpm.map.MapPmView
import me.dmdev.rxpm.map.base.MapPmSupportActivity

/**
 * Delegate for the [Activity] that helps with creation and binding of
 * a [presentation model][PresentationModel] and a [MapPmView].
 *
 * Use this class only if you can't subclass the [MapPmSupportActivity].
 *
 * Users of this class must forward all the life cycle methods from the containing Activity
 * to the corresponding ones in this class.
 */
class MapPmActivityDelegate<PM, A>(private val mapPmView:A)
where PM : PresentationModel, PM : MapPmExtension, A : Activity, A : MapPmView<PM> {

    private val pmDelegate = PmActivityDelegate(mapPmView)
    private val mapPmViewDelegate by lazy {
        MapPmViewDelegate(pmDelegate.presentationModel,
                          mapPmView,
                          pmDelegate.pmBinder)
    }

    val presentationModel get() = pmDelegate.presentationModel

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onCreate(savedInstanceState: Bundle?) {
        pmDelegate.onCreate(savedInstanceState)
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onPostCreate(contentView: View, savedInstanceState: Bundle?) {
        mapPmViewDelegate.onCreateMapView(contentView, savedInstanceState)
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onStart() {
        pmDelegate.onStart()
        mapPmViewDelegate.onStart()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onResume() {
        pmDelegate.onResume()
        mapPmViewDelegate.onResume()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onPause() {
        pmDelegate.onPause()
        mapPmViewDelegate.onPause()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onSaveInstanceState(outState: Bundle) {
        pmDelegate.onSaveInstanceState(outState)
        mapPmViewDelegate.onSaveInstanceState(outState)
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onStop() {
        pmDelegate.onStop()
        mapPmViewDelegate.onStop()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onDestroy() {
        pmDelegate.onDestroy()
        mapPmViewDelegate.onDestroyMapView()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onLowMemory() {
        mapPmViewDelegate.onLowMemory()
    }
}