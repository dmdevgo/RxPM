package me.dmdev.rxpm.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import me.dmdev.rxpm.base.PmSupportFragment
import timber.log.Timber

/**
 * @author Dmitriy Gorbunov
 */
class MainSupportFragment : PmSupportFragment<FragmentPm>() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FrameLayout(context)
    }

    override fun providePresentationModel(): FragmentPm {
        Timber.i("providePresentationModel")
        return FragmentPm()
    }

    override fun onBindPresentationModel(pm: FragmentPm) {
        Timber.i("onBindPresentationModel")
    }

    override fun onUnbindPresentationModel() {
        Timber.i("onUnbindPresentationModel")
    }

}