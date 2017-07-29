package me.dmdev.rxpm.delegate

import android.app.Activity
import android.os.Bundle
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.Lifecycle
import me.jeevuz.outlast.Outlasting
import me.jeevuz.outlast.predefined.ActivityOutlast

/**
 * @author Dmitriy Gorbunov
 */
class PmActivityDelegate<out PM : PresentationModel>(private val pmView: PmView<PM>) {

    init {
        require(pmView is Activity) {"This class can be used only with Activity PmView!"}
    }

    private lateinit var outlast: ActivityOutlast<PmWrapper<PM>>
    private var binded = false

    val presentationModel: PM by lazy { outlast.outlasting.presentationModel }

    fun onCreate(savedInstanceState: Bundle?) {
        outlast = ActivityOutlast(pmView as Activity,
                                  Outlasting.Creator<PmWrapper<PM>> {
                                              PmWrapper(pmView.providePresentationModel())
                                          },
                                  savedInstanceState)
        presentationModel // Create lazy presentation model now
    }

    fun onStart() {
        outlast.onStart()
        bind()
    }

    fun onResume() {
        outlast.onResume()
        bind()
    }

    fun onSaveInstanceState(outState: Bundle) {
        outlast.onSaveInstanceState(outState)
        unbind()
    }

    fun onPause() {
        // For symmetry, may be used in the future
    }

    fun onStop() {
        unbind()
    }

    fun onDestroy() {
        outlast.onDestroy()
    }

    private fun bind() {
        if (!binded) {
            pmView.onBindPresentationModel(presentationModel)
            presentationModel.lifecycleConsumer.accept(Lifecycle.BINDED)
            binded = true
        }
    }

    private fun unbind() {
        if (binded) {
            presentationModel.lifecycleConsumer.accept(Lifecycle.UNBINDED)
            pmView.onUnbindPresentationModel()
            pmView.compositeUnbind.clear()
            binded = false
        }
    }

}