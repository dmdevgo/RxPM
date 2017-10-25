package me.dmdev.rxpm.navigation

/**
 * @since 1.1
 */
interface NavigationMessageHandler {
    fun handleNavigationMessage(msg: NavigationMessage): Boolean
}