package me.dmdev.rxpm.delegate

import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.Lifecycle

/**
 * @author Dmitriy Gorbunov
 */
class PmControllerDelegate<PM : PresentationModel>(private val pmView: PmView<PM>) {

    internal lateinit var pmBinder: PmBinder<PM>
    private var created = false

    val presentationModel: PM by lazy { pmView.providePresentationModel() }

    private fun onCreate() {
        presentationModel.lifecycleConsumer.accept(Lifecycle.CREATED)
        pmBinder = PmBinder(presentationModel, pmView)
    }

    fun onCreateView() {
        if (!created) {
            created = true
            onCreate()
        }
    }

    fun onAttach() {
        pmBinder.bind()
    }

    fun onDetach() {
        pmBinder.unbind()
    }

    fun onDestroyView() {
        // May be used in the future
    }

    fun onDestroy() {
        presentationModel.lifecycleConsumer.accept(Lifecycle.DESTROYED)
    }
}