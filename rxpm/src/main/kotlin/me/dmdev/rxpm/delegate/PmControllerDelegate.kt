package me.dmdev.rxpm.delegate

import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.Lifecycle

/**
 * @author Dmitriy Gorbunov
 */
class PmControllerDelegate<out PM : PresentationModel>(pmView: PmView<PM>) {

    val presentationModel:PM = pmView.providePresentationModel()
    private val binder = PmBinder(presentationModel, pmView)

    fun onCreate() {
        presentationModel.lifecycleConsumer.accept(Lifecycle.CREATED)
    }

    fun onCreateView() {
        // May be used in the future
    }

    fun onAttach() {
        binder.bind()
    }

    fun onDetach() {
        binder.unbind()
    }

    fun onDestroyView() {
        // May be used in the future
    }

    fun onDestroy() {
        presentationModel.lifecycleConsumer.accept(Lifecycle.DESTROYED)
    }
}