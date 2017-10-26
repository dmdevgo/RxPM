package me.dmdev.rxpm.navigation

/**
 * Interface for classes which implement navigation in the app.
 *
 * [Navigation messages][NavigationMessage] are dispatched up the hierarchy tree from child to parent
 * (e.g. from Fragment to it's parent Fragment and then to the Activity).
 * Any class in the chain that implements the interface can intercept the message and handle it.
 * If [handleNavigationMessage] returns true, the message will be treated as consumed and will not go further.
 *
 * @since 1.1
 */
interface NavigationMessageHandler {

    /**
     * Handles the [navigation message][NavigationMessage].
     * @param message the navigation message.
     * @return true if [message] was handled, false otherwise.
     */
    fun handleNavigationMessage(message: NavigationMessage): Boolean
}