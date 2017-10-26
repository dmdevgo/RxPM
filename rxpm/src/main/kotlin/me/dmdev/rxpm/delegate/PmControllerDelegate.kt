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

    internal lateinit var pmBinder: PmBinder<PM>
    private var created = false

    private lateinit var navigationMessagesDisposable: Disposable
    private val navigationMessageDispatcher = ControllerNavigationMessageDispatcher(pmView)

    val presentationModel: PM by lazy { pmView.providePresentationModel() }

    private fun onCreate() {
        presentationModel.lifecycleConsumer.accept(Lifecycle.CREATED)
        pmBinder = PmBinder(presentationModel, pmView)
        navigationMessagesDisposable = presentationModel.navigationMessages.observable.subscribe {
            navigationMessageDispatcher.dispatch(it)
        }
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
        navigationMessagesDisposable.dispose()
        presentationModel.lifecycleConsumer.accept(Lifecycle.DESTROYED)
    }
}