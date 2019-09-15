package me.dmdev.rxpm.delegate

import android.view.*
import com.bluelinelabs.conductor.*
import me.dmdev.rxpm.*
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
class PmControllerDelegate<PM, C>(pmController: C)
        where PM : PresentationModel,
              C : Controller, C : PmView<PM> {

    private var created = false

    private val commonDelegate =
        CommonDelegate<PM, C>(pmController, ControllerNavigationMessageDispatcher(pmController))

    val presentationModel: PM get() = commonDelegate.presentationModel

    init {
        pmController.addLifecycleListener(object : Controller.LifecycleListener() {

            override fun preCreateView(controller: Controller) {
                super.preCreateView(controller)
                if (!created) {
                    commonDelegate.onCreate(null)
                    created = true
                }
            }

            override fun postCreateView(controller: Controller, view: View) {
                commonDelegate.onBind()
            }

            override fun postAttach(controller: Controller, view: View) {
                commonDelegate.onResume()
            }

            override fun preDetach(controller: Controller, view: View) {
                commonDelegate.onPause()
            }

            override fun preDestroyView(controller: Controller, view: View) {
                commonDelegate.onUnbind()
            }

            override fun preDestroy(controller: Controller) {
                if (created) {
                    commonDelegate.onDestroy()
                }
            }
        })
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        commonDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}