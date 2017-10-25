package me.dmdev.rxpm.navigation

import com.bluelinelabs.conductor.Controller

/**
 * @since 1.1
 */
internal class ControllerNavigationMessageDispatcher(controller: Controller) : NavigationMessageDispatcher(controller) {
    override fun getParent(any: Any?): Any? {
        return if (any is Controller) {
            any.parentController ?: any.activity
        } else {
            null
        }
    }
}