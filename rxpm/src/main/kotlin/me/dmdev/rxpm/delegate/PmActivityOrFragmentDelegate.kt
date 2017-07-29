package me.dmdev.rxpm.delegate

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.Lifecycle
import me.jeevuz.outlast.Outlast
import me.jeevuz.outlast.Outlasting
import me.jeevuz.outlast.predefined.ActivityOutlast
import me.jeevuz.outlast.predefined.FragmentOutlast

/**
 * @author Dmitriy Gorbunov
 */
class PmActivityOrFragmentDelegate<out PM : PresentationModel>(private val pmView: PmView<PM>) {

    private lateinit var outlast: Outlast<PmWrapper<PM>>
    private var binded = false

    val presentationModel: PM by lazy { outlast.outlasting.presentationModel }

    fun onCreate(savedInstanceState: Bundle?) {

        val outlastingCreator = Outlasting.Creator<PmWrapper<PM>> {
            PmWrapper(pmView.providePresentationModel())
        }

        // Choose outlast implementation
        outlast = when(pmView) {
            is Activity -> ActivityOutlast(pmView, outlastingCreator, savedInstanceState)
            is Fragment -> FragmentOutlast(pmView, outlastingCreator, savedInstanceState)

            else -> throw IllegalArgumentException(
                    "This class can be used only with PmView that is Activity or Fragment")
        }

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