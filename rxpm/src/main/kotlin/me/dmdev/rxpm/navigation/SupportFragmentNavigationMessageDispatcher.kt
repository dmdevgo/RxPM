package me.dmdev.rxpm.navigation

import android.support.v4.app.Fragment

/**
 * @since 1.1
 */
internal class SupportFragmentNavigationMessageDispatcher(fragment: Fragment) : NavigationMessageDispatcher(fragment) {
    override fun getParent(any: Any?): Any? {
        return if (any is Fragment) {
            any.parentFragment ?: any.activity
        } else {
            null
        }
    }
}