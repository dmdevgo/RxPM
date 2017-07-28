package me.dmdev.rxpm.delegate

import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.Lifecycle

/**
 * @author Dmitriy Gorbunov
 */
class PmControllerDelegate<out PM : PresentationModel>(private val pmView: PmView<PM>) {

    val pm :PM = pmView.providePresentationModel()

    fun onCreate() {
        pm.lifecycleConsumer.accept(Lifecycle.CREATED)
    }

    fun onCreateView() {
        pmView.onBindPresentationModel(pm)
        pm.lifecycleConsumer.accept(Lifecycle.BINDED)
    }

    fun onAttach() {}

    fun onDetach() {}

    fun onDestroyView() {
        pm.lifecycleConsumer.accept(Lifecycle.UNBINDED)
        pmView.onUnbindPresentationModel()
        pmView.compositeUnbind.clear()
    }

    fun onDestroy() {
        pm.lifecycleConsumer.accept(Lifecycle.DESTROYED)
    }
}