package me.dmdev.rxpm.navigation

import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith

class NavigationMessageDispatcherTest {

    private val testMessage = object : NavigationMessage {}

    private lateinit var workingHandler: NavigationMessageHandler
    private lateinit var ignoringHandler: NavigationMessageHandler
    private lateinit var unreachableHandler: NavigationMessageHandler

    @Before fun setUp() {
        workingHandler = mockMessageHandler(true)
        ignoringHandler = mockMessageHandler(false)
        unreachableHandler = mockMessageHandler(true)
    }

    private fun mockMessageHandler(handleMessages: Boolean): NavigationMessageHandler {
        return mock {
            on { handleNavigationMessage(any()) } doReturn handleMessages
        }
    }

    @Test fun handleMessage() {
        val dispatcher = createDispatcher(listOf(Unit, ignoringHandler, workingHandler, unreachableHandler))

        dispatcher.dispatch(testMessage)

        verify(ignoringHandler).handleNavigationMessage(testMessage)
        verify(workingHandler).handleNavigationMessage(testMessage)
        verifyZeroInteractions(unreachableHandler)
    }

    private fun createDispatcher(handlers: List<Any>): NavigationMessageDispatcher {
        return object : NavigationMessageDispatcher(Unit) {

            var k = 0

            override fun getParent(node: Any?): Any? {

                val result: Any? = try {
                    handlers[k]
                } catch (e: ArrayIndexOutOfBoundsException) {
                    null
                }
                k++

                return result
            }
        }
    }

    @Test fun failsIfMessageNotHandled() {
        val dispatcher = createDispatcher(listOf(Unit, ignoringHandler))

        assertFailsWith<NotHandledNavigationMessageException> {
            dispatcher.dispatch(testMessage)
        }
    }
}