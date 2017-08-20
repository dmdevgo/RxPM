package me.dmdev.rxpm.sample.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.base.PmSupportFragment

/**
 * @author Dmitriy Gorbunov
 */
abstract class BaseFragment<PM : PresentationModel> : PmSupportFragment<PM>() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getFragmentLayout(), container, false)
    }

    @LayoutRes protected abstract fun getFragmentLayout(): Int

}