package me.dmdev.rxpm.navigation

import androidx.fragment.app.Fragment

internal class FragmentNavigationMessageDispatcher(
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