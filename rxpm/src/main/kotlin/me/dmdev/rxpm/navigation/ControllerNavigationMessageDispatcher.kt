package me.dmdev.rxpm.navigation

import com.bluelinelabs.conductor.Controller

internal class ControllerNavigationMessageDispatcher(
    controller: Controller
) : NavigationMessageDispatcher(controller) {

    override fun getParent(node: Any?): Any? {
        return if (node is Controller) {
            node.parentController ?: node.activity
        } else {
            null
        }
    }
}