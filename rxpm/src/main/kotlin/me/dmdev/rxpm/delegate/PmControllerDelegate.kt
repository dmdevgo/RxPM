package me.dmdev.rxpm.delegate

import com.bluelinelabs.conductor.Controller
import io.reactivex.disposables.Disposable
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.Lifecycle
import me.dmdev.rxpm.base.PmController
import me.dmdev.rxpm.navigation.ControllerNavigationMessageDispatcher

/**
 * Delegate for the [Controller] that helps with creation and binding of
 * a [presentation model][PresentationModel] and a [view][PmView].
 *
 * Use this class only if you can't subclass the [PmController].
 *
 * Users of this class must forward all the life cycle methods from the containing Controller
 * to the corresponding ones in this class.
 */
class PmControllerDelegate<PM, C>(private val pmView: C)
where PM : PresentationModel, C : Controller, C : PmView<PM> {

    internal val pmBinder: PmBinder<PM> by lazy(LazyThreadSafetyMode.NONE) { PmBinder(presentationModel, pmView) }
    private var created = false

    private val navigationMessageDispatcher = ControllerNavigationMessageDispatcher(pmView)
    private var navigationMessagesDisposable: Disposable? = null

    val presentationModel: PM by lazy(LazyThreadSafetyMode.NONE) { pmView.providePresentationModel() }

    private fun onCreate() {
        presentationModel.lifecycleConsumer.accept(Lifecycle.CREATED)
        navigationMessagesDisposable = presentationModel.navigationMessages.observable.subscribe {
            navigationMessageDispatcher.dispatch(it)
        }
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onCreateView() {
        if (!created) {
            onCreate()
            created = true
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
        navigationMessagesDisposable?.dispose()
        if (created) {
            presentationModel.lifecycleConsumer.accept(Lifecycle.DESTROYED)
        }
    }
}