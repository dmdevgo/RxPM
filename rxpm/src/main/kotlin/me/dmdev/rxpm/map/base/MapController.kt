package me.dmdev.rxpm.map.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RestoreViewOnCreateController
import io.reactivex.disposables.CompositeDisposable
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.map.MapPmExtension
import me.dmdev.rxpm.map.MapPmView
import me.dmdev.rxpm.map.delegate.MapPmControllerDelegate

/**
 * @author Dmitriy Gorbunov
 */
abstract class MapController<PM>(args: Bundle? = null) : RestoreViewOnCreateController(args),
                                                         MapPmView<PM>
where PM : PresentationModel, PM : MapPmExtension {

    @Suppress("LeakingThis")
    private val delegate: MapPmControllerDelegate<PM> = MapPmControllerDelegate(this)

    final override val compositeUnbind = CompositeDisposable()

    final override val presentationModel get() = delegate.presentationModel

    init {
        delegate.onCreate()
    }

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        val view = createView(inflater, container, savedViewState)
        onInitView(view, savedViewState)
        delegate.onCreateView(view, savedViewState)
        return view
    }

    abstract fun createView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View

    open fun onInitView(view: View, savedViewState: Bundle?) {
        //override this for init views
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        delegate.onAttach()
    }

    override fun onDetach(view: View) {
        super.onDetach(view)
        delegate.onDetach()
    }

    override fun onSaveViewState(view: View, outState: Bundle) {
        super.onSaveViewState(view, outState)
        delegate.onSaveViewState(outState)
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        delegate.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        delegate.onDestroy()
    }

    // Call from outside
    fun onLowMemory() {
        delegate.onLowMemory()
    }
}