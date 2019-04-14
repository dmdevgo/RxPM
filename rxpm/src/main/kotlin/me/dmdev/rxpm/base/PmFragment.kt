package me.dmdev.rxpm.base

import android.os.*
import android.support.v4.app.*
import android.view.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.delegate.*

/**
 * Predefined [Fragment] implementing the [PmView][PmView].
 *
 * Just override the [providePresentationModel] and [onBindPresentationModel] methods and you are good to go.
 *
 * If extending is not possible you can implement [PmView],
 * create a [PmFragmentDelegate] and pass the lifecycle callbacks to it.
 * See this class's source code for the example.
 */
abstract class PmFragment<PM : PresentationModel> : Fragment(), PmView<PM> {

    private val delegate by lazy(LazyThreadSafetyMode.NONE) { PmFragmentDelegate(this) }

    final override val presentationModel get() = delegate.presentationModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delegate.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        delegate.onViewCreated()
    }

    override fun onStart() {
        super.onStart()
        delegate.onStart()
    }

    override fun onResume() {
        super.onResume()
        delegate.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        delegate.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        delegate.onPause()
        super.onPause()
    }

    override fun onStop() {
        delegate.onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        delegate.onDestroyView()
        super.onDestroyView()
    }

    override fun onDestroy() {
        delegate.onDestroy()
        super.onDestroy()
    }
}