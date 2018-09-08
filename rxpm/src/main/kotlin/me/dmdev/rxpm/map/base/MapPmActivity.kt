package me.dmdev.rxpm.map.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import io.reactivex.disposables.CompositeDisposable
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.map.MapPmExtension
import me.dmdev.rxpm.map.MapPmView
import me.dmdev.rxpm.map.delegate.MapPmActivityDelegate

/**
 * Predefined [Activity][AppCompatActivity] implementing the [MapPmView].
 *
 * Just override the [providePresentationModel], [onBindPresentationModel]
 * and [onBindMapPresentationModel] methods and you are good to go.
 *
 * If extending is not possible you can implement [MapPmView],
 * create a [MapPmActivityDelegate] and pass the lifecycle callbacks to it.
 * See this class's source code for the example.
 */
abstract class MapPmActivity<PM> : AppCompatActivity(), MapPmView<PM>
        where PM : PresentationModel, PM : MapPmExtension {

    private val delegate by lazy(LazyThreadSafetyMode.NONE) { MapPmActivityDelegate(this) }

    final override val compositeUnbind = CompositeDisposable()

    final override val presentationModel get() = delegate.presentationModel

    final override var mapView: MapView? = null
    final override var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delegate.onCreate(savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        delegate.onPostCreate(this.findViewById(android.R.id.content), savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        delegate.onStart()
    }

    override fun onResume() {
        super.onResume()
        delegate.onResume()
    }

    override fun onPause() {
        super.onPause()
        delegate.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        delegate.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        delegate.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        delegate.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        delegate.onLowMemory()
    }
}