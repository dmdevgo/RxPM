package me.dmdev.rxpm.navigation

internal abstract class NavigationMessageDispatcher(private val firstNode: Any) {

    fun dispatch(message: NavigationMessage) {

        var node: Any? = firstNode

        do {
            if (node is NavigationMessageHandler && node.handleNavigationMessage(message)) {
                return
            }
            node = getParent(node)
        } while (node != null)

        throw NotHandledNavigationMessageException()
    }

    abstract fun getParent(node: Any?): Any?
}