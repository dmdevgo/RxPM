package me.dmdev.rxpm.conductor

import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.LifeCycleState

/**
 * @author Dmitriy Gorbunov
 */
class PmControllerDelegate<PM : PresentationModel>(private val pmView: PmView<PM>) {

    lateinit var pm :PM

    fun onCreate() {
        pm = pmView.providePresentationModel()
        pm.lifeCycleConsumer.accept(LifeCycleState.ON_CREATE)
    }

    fun onCreateView() {
        pmView.onBindPresentationModel()
        pm.lifeCycleConsumer.accept(LifeCycleState.ON_BIND)
    }

    fun onAttach() {}

    fun onDetach() {}

    fun onDestroyView() {
        pm.lifeCycleConsumer.accept(LifeCycleState.ON_UNBIND)
        pmView.onUnbindPresentationModel()
        pmView.compositeDisposable.clear()
    }

    fun onDestroy() {
        pm.lifeCycleConsumer.accept(LifeCycleState.ON_DESTROY)
    }
}