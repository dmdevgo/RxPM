package me.dmdev.rxpm.navigation

/**
 * @since 1.1
 */
internal abstract class NavigationMessageDispatcher(private val from: Any) {

    fun dispatch(message: NavigationMessage) {

        var any: Any? = from

        do {
            if (any is NavigationMessageHandler && any.handleNavigationMessage(message)) {
                return
            }
            any = getParent(any)
        } while (any != null)

        throw NotHandledNavigationMessage("")
    }

    abstract fun getParent(any: Any?): Any?
}