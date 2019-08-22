package me.dmdev.rxpm.navigation

import android.app.*

class ActivityNavigationMessageDispatcher(
    activity: Activity
) : NavigationMessageDispatcher(activity) {

    override fun getParent(node: Any?): Any? = null
}