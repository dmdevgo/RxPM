package me.dmdev.rxpm.delegate

import com.bluelinelabs.conductor.Controller
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.Lifecycle
import me.dmdev.rxpm.base.PmController

/**
 * Delegate for the [Controller] that helps with creation and binding of
 * a [presentation model][PresentationModel] and a [view][PmView].
 *
 * Use this class only if you can't subclass the [PmController].
 *
 * Users of this class must forward all the life cycle methods from the containing Controller
 * to the corresponding ones in this class.
 */
class PmControllerDelegate<PM : PresentationModel>(private val pmView: PmView<PM>) {

    internal lateinit var pmBinder: PmBinder<PM>
    private var created = false

    val presentationModel: PM by lazy { pmView.providePresentationModel() }

    private fun onCreate() {
        presentationModel.lifecycleConsumer.accept(Lifecycle.CREATED)
        pmBinder = PmBinder(presentationModel, pmView)
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onCreateView() {
        if (!created) {
            created = true
            onCreate()
        }
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onAttach() {
        pmBinder.bind()
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onDetach() {
        pmBinder.unbind()
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onDestroyView() {
        // May be used in the future
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onDestroy() {
        presentationModel.lifecycleConsumer.accept(Lifecycle.DESTROYED)
    }
}