package me.dmdev.rxpm.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RestoreViewOnCreateController
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.delegate.PmControllerDelegate

/**
 * @author Dmitriy Gorbunov
 */
abstract class PmController<out PM : PresentationModel>(args: Bundle? = null) : RestoreViewOnCreateController(args), PmView<PM> {

    @Suppress("LeakingThis")
    private val delegate: PmControllerDelegate<PM> = PmControllerDelegate(this)

    final override val presentationModel get() = delegate.pm

    init {
        delegate.onCreate()
    }

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        val view = createView(inflater, container, savedViewState)
        onInitView(view, savedViewState)
        delegate.onCreateView()
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

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        delegate.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        delegate.onDestroy()
    }

}