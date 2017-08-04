package me.dmdev.rxpm.delegate

import android.app.Activity
import android.os.Bundle
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.jeevuz.outlast.Outlasting
import me.jeevuz.outlast.predefined.ActivityOutlast

/**
 * @author Dmitriy Gorbunov
 */
class PmActivityDelegate<PM : PresentationModel>(private val pmView: PmView<PM>) {

    init {
        require(pmView is Activity) {"This class can be used only with Activity PmView!"}
    }

    private lateinit var outlast: ActivityOutlast<PmWrapper<PM>>
    internal lateinit var pmBinder: PmBinder<PM>

    val presentationModel: PM by lazy { outlast.outlasting.presentationModel }

    fun onCreate(savedInstanceState: Bundle?) {
        outlast = ActivityOutlast(pmView as Activity,
                                  Outlasting.Creator<PmWrapper<PM>> {
                                              PmWrapper(pmView.providePresentationModel())
                                          },
                                  savedInstanceState)
        presentationModel // Create lazy presentation model now
        pmBinder = PmBinder(presentationModel, pmView)
    }

    fun onStart() {
        outlast.onStart()
        pmBinder.bind()
    }

    fun onResume() {
        outlast.onResume()
        pmBinder.bind()
    }

    fun onSaveInstanceState(outState: Bundle) {
        outlast.onSaveInstanceState(outState)
        pmBinder.unbind()
    }

    fun onPause() {
        // For symmetry, may be used in the future
    }

    fun onStop() {
        pmBinder.unbind()
    }

    fun onDestroy() {
        outlast.onDestroy()
    }

}