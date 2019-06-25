package me.dmdev.rxpm.navigation

import me.dmdev.rxpm.*

interface NavigationalPm {

    /**
     * Command to send [navigation message][NavigationMessage] to the [NavigationMessageHandler].
     */
    val navigationMessages: Command<NavigationMessage>
}