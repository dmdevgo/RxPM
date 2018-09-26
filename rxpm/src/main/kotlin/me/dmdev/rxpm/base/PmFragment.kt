package me.dmdev.rxpm.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import me.dmdev.rxpm.AndroidPmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.delegate.PmFragmentDelegate

/**
 * Predefined [Fragment] implementing the [PmView][AndroidPmView].
 *
 * Just override the [providePresentationModel] and [onBindPresentationModel] methods and you are good to go.
 *
 * If extending is not possible you can implement [AndroidPmView],
 * create a [PmFragmentDelegate] and pass the lifecycle callbacks to it.
 * See this class's source code for the example.
 */
abstract class PmFragment<PM : PresentationModel> : Fragment(), AndroidPmView<PM> {

    private val delegate by lazy(LazyThreadSafetyMode.NONE) { PmFragmentDelegate(this) }

    final override val compositeUnbind = CompositeDisposable()

    final override val presentationModel get() = delegate.presentationModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delegate.onCreate(savedInstanceState)
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

    override fun onDestroy() {
        delegate.onDestroy()
        super.onDestroy()
    }
}