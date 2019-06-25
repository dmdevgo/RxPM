package me.dmdev.rxpm.map.base

import android.os.*
import android.support.v4.app.*
import android.view.*
import com.google.android.gms.maps.*
import io.reactivex.disposables.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.map.*
import me.dmdev.rxpm.map.delegate.*

/**
 * Predefined [Fragment] implementing the [MapPmView].
 *
 * Just override the [providePresentationModel], [onBindPresentationModel]
 * and [onBindMapPresentationModel] methods and you are good to go.
 *
 * If extending is not possible you can implement [MapPmView],
 * create a [MapPmSupportFragmentDelegate] and pass the lifecycle callbacks to it.
 * See this class's source code for the example.
 */
abstract class MapPmSupportFragment<PM> : Fragment(), MapPmView<PM>
        where PM : PresentationModel, PM : MapPmExtension {

    private val delegate by lazy(LazyThreadSafetyMode.NONE) { MapPmSupportFragmentDelegate(this) }

    final override val compositeUnbind = CompositeDisposable()

    final override val presentationModel get() = delegate.presentationModel

    final override var mapView: MapView? = null
    final override var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delegate.onCreate(savedInstanceState)
    }

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  createView(inflater, container, savedInstanceState)
        delegate.onCreateView(view, savedInstanceState)
        return view
    }

    /**
     * Replaces the [onCreateView] that the library hides for internal use.
     */
    abstract fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View

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

    override fun onDestroyView() {
        super.onDestroyView()
        delegate.onDestroyView()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        delegate.onLowMemory()
    }
}