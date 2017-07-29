package me.dmdev.rxpm.map.base

import android.os.Bundle
import me.dmdev.rxpm.base.PmSupportActivity
import me.dmdev.rxpm.map.MapPmView
import me.dmdev.rxpm.map.MapPresentationModel
import me.dmdev.rxpm.map.delegate.MapActivityDelegate

/**
 * @author Dmitriy Gorbunov
 */
abstract class MapSupportActivity<PM : MapPresentationModel> : PmSupportActivity<PM>(),
                                                               MapPmView<PM> {

    private lateinit var mapDelegate: MapActivityDelegate<PM>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapDelegate = MapActivityDelegate(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mapDelegate.onPostCreate(this.findViewById(android.R.id.content), savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        mapDelegate.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapDelegate.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapDelegate.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapDelegate.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapDelegate.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapDelegate.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapDelegate.onLowMemory()
    }
}