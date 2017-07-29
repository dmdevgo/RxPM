package me.dmdev.rxpm.map.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.dmdev.rxpm.base.PmSupportFragment
import me.dmdev.rxpm.map.MapPmView
import me.dmdev.rxpm.map.MapPresentationModel
import me.dmdev.rxpm.map.delegate.MapPmSupportFragmentDelegate

/**
 * @author Dmitriy Gorbunov
 */
abstract class MapSupportFragment<PM : MapPresentationModel> : PmSupportFragment<PM>(),
                                                               MapPmView<PM> {

    private lateinit var mapDelegate: MapPmSupportFragmentDelegate<PM>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapDelegate = MapPmSupportFragmentDelegate(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState).apply {
            mapDelegate.onCreateView(this!!, savedInstanceState)
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        mapDelegate.onDestroyView()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapDelegate.onLowMemory()
    }
}