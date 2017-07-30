package me.dmdev.rxpm.delegate

import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.Lifecycle

/**
 * @author Dmitriy Gorbunov
 */
class PmControllerDelegate<out PM : PresentationModel>(private val pmView: PmView<PM>) {

    val presentationModel:PM = pmView.providePresentationModel()

    fun onCreate() {
        presentationModel.lifecycleConsumer.accept(Lifecycle.CREATED)
    }

    fun onCreateView() {
        // May be used in the future
    }

    fun onAttach() {
        pmView.onBindPresentationModel(presentationModel)
        presentationModel.lifecycleConsumer.accept(Lifecycle.BINDED)
    }

    fun onDetach() {
        presentationModel.lifecycleConsumer.accept(Lifecycle.UNBINDED)
        pmView.onUnbindPresentationModel()
        pmView.compositeUnbind.clear()
    }

    fun onDestroyView() {
        // May be used in the future
    }

    fun onDestroy() {
        presentationModel.lifecycleConsumer.accept(Lifecycle.DESTROYED)
    }
}