package me.dmdev.rxpm.navigation

import android.app.Activity

/**
 * @since 1.1
 */
internal class ActivityNavigationMessageDispatcher(activity: Activity) : NavigationMessageDispatcher(activity) {
    override fun getParent(any: Any?): Any? = null
}