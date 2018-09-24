package me.dmdev.rxpm.navigation

import android.support.v4.app.Fragment

internal class SupportFragmentNavigationMessageDispatcher(
    fragment: Fragment
) : NavigationMessageDispatcher(fragment) {

    override fun getParent(node: Any?): Any? {
        return if (node is Fragment) {
            node.parentFragment ?: node.activity
        } else {
            null
        }
    }
}