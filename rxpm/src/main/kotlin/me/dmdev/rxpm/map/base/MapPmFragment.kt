package me.dmdev.rxpm.map.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import io.reactivex.disposables.CompositeDisposable
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.map.MapPmExtension
import me.dmdev.rxpm.map.MapPmView
import me.dmdev.rxpm.map.delegate.MapPmFragmentDelegate

/**
 * Predefined [Fragment] implementing the [MapPmView].
 *
 * Just override the [providePresentationModel], [onBindPresentationModel]
 * and [onBindMapPresentationModel] methods and you are good to go.
 *
 * If extending is not possible you can implement [MapPmView],
 * create a [MapPmFragmentDelegate] and pass the lifecycle callbacks to it.
 * See this class's source code for the example.
 */
abstract class MapPmFragment<PM> : Fragment(), MapPmView<PM>
        where PM : PresentationModel, PM : MapPmExtension {

    private val delegate by lazy(LazyThreadSafetyMode.NONE) { MapPmFragmentDelegate(this) }

    final override val compositeUnbind = CompositeDisposable()

    final override val presentationModel get() = delegate.presentationModel

    final override var mapView: MapView? = null
    final override var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delegate.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState).apply {
            delegate.onCreateView(this!!, savedInstanceState)
        }
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
        delegate.onPause()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        delegate.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        delegate.onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        delegate.onDestroyView()
        super.onDestroyView()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        delegate.onLowMemory()
    }
}