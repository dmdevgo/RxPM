package me.dmdev.rxpm.delegate

import android.os.Bundle
import android.support.v4.app.Fragment
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.jeevuz.outlast.Outlasting
import me.jeevuz.outlast.predefined.FragmentOutlast

/**
 * @author Dmitriy Gorbunov
 */
class PmSupportFragmentDelegate<out PM : PresentationModel>(private val pmView: PmView<PM>) {

    init {
        require(pmView is Fragment) {"This class can be used only with support Fragment PmView!"}
    }

    private lateinit var outlast: FragmentOutlast<PmWrapper<PM>>
    private lateinit var binder: PmBinder<PM>

    val presentationModel: PM by lazy { outlast.outlasting.presentationModel }

    fun onCreate(savedInstanceState: Bundle?) {
        outlast = FragmentOutlast(pmView as Fragment,
                                  Outlasting.Creator<PmWrapper<PM>> {
                                              PmWrapper(pmView.providePresentationModel())
                                          },
                                  savedInstanceState)
        presentationModel // Create lazy presentation model now
        binder = PmBinder(presentationModel, pmView)
    }

    fun onStart() {
        outlast.onStart()
        binder.bind()
    }

    fun onResume() {
        outlast.onResume()
        binder.bind()
    }

    fun onSaveInstanceState(outState: Bundle) {
        outlast.onSaveInstanceState(outState)
        binder.unbind()
    }

    fun onPause() {
        // For symmetry, may be used in the future
    }

    fun onStop() {
        binder.unbind()
    }

    fun onDestroy() {
        outlast.onDestroy()
    }
}
