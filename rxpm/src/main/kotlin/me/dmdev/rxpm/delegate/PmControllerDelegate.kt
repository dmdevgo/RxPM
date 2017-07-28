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
        pmView.onBindPresentationModel(presentationModel)
        presentationModel.lifecycleConsumer.accept(Lifecycle.BINDED)
    }

    fun onAttach() {}

    fun onDetach() {}

    fun onDestroyView() {
        presentationModel.lifecycleConsumer.accept(Lifecycle.UNBINDED)
        pmView.onUnbindPresentationModel()
        pmView.compositeUnbind.clear()
    }

    fun onDestroy() {
        presentationModel.lifecycleConsumer.accept(Lifecycle.DESTROYED)
    }
}