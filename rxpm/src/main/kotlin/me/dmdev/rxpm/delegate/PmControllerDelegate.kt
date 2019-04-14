package me.dmdev.rxpm.delegate

import android.view.*
import com.bluelinelabs.conductor.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.PresentationModel.*
import me.dmdev.rxpm.base.*
import me.dmdev.rxpm.navigation.*

/**
 * Delegate for the [Controller] that helps with creation and binding of
 * a [presentation model][PresentationModel] and a [view][PmView].
 *
 * Use this class only if you can't subclass the [PmController].
 *
 * Users of this class must forward all the life cycle methods from the containing Controller
 * to the corresponding ones in this class.
 */
class PmControllerDelegate<PM, C>(private val pmController: C)
        where PM : PresentationModel,
              C : Controller, C : PmView<PM> {

    private var created = false

    private val navigationMessageDispatcher = ControllerNavigationMessageDispatcher(pmController)

    val presentationModel: PM by lazy(LazyThreadSafetyMode.NONE) { pmController.providePresentationModel() }

    init {
        pmController.addLifecycleListener(object: Controller.LifecycleListener() {

            override fun preCreateView(controller: Controller) {
                super.preCreateView(controller)
                if (!created) {
                    presentationModel.lifecycleConsumer.accept(Lifecycle.CREATED)
                    created = true
                }
            }

            override fun postCreateView(controller: Controller, view: View) {
                presentationModel.lifecycleConsumer.accept(Lifecycle.BINDED)
                presentationModel.navigationMessages bindTo {
                    navigationMessageDispatcher.dispatch(it)
                }
            }

            override fun postAttach(controller: Controller, view: View) {
                super.postAttach(controller, view)
                presentationModel.lifecycleConsumer.accept(Lifecycle.RESUMED)
            }

            override fun preDetach(controller: Controller, view: View) {
                super.preDetach(controller, view)
                presentationModel.lifecycleConsumer.accept(Lifecycle.PAUSED)
            }

            override fun preDestroyView(controller: Controller, view: View) {
                super.preDestroyView(controller, view)
                presentationModel.lifecycleConsumer.accept(Lifecycle.UNBINDED)
            }

            override fun preDestroy(controller: Controller) {
                super.preDestroy(controller)
                if (created) {
                    presentationModel.lifecycleConsumer.accept(Lifecycle.DESTROYED)
                }
            }
        })
    }
}