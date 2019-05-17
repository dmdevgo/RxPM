package me.dmdev.rxpm.navigation

import me.dmdev.rxpm.Command

interface NavigationalPm {

    /**
     * Command to send [navigation message][NavigationMessage] to the [NavigationMessageHandler].
     * @since 1.1
     */
    val navigationMessages: Command<NavigationMessage>
}