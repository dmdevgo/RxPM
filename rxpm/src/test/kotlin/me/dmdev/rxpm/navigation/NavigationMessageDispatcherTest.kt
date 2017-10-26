package me.dmdev.rxpm.navigation

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class NavigationMessageDispatcherTest {

    @Mock lateinit var handledNavigator: NavigationMessageHandler
    @Mock lateinit var notHandledNavigator: NavigationMessageHandler
    @Mock lateinit var unreachableNavigator: NavigationMessageHandler
    private val testMessage = object : NavigationMessage {}

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(handledNavigator.handleNavigationMessage(testMessage)).thenReturn(true)
        Mockito.`when`(notHandledNavigator.handleNavigationMessage(testMessage)).thenReturn(false)
    }

    @Test
    fun testHandledMessage() {

        val dispatcher = object : NavigationMessageDispatcher(Unit) {

            var k = 0

            override fun getParent(node: Any?): Any? {

                val result: Any? = when (k) {
                    0 -> Unit
                    1 -> notHandledNavigator
                    2 -> handledNavigator
                    3 -> unreachableNavigator
                    else -> null
                }
                k++
                return result
            }
        }

        dispatcher.dispatch(testMessage)

        Mockito.verify(notHandledNavigator).handleNavigationMessage(testMessage)
        Mockito.verify(handledNavigator).handleNavigationMessage(testMessage)
        Mockito.verifyZeroInteractions(unreachableNavigator)

    }

    @Test(expected = NotHandledNavigationMessageException::class)
    fun testNotHandledMessage() {

        val dispatcher = object : NavigationMessageDispatcher(Unit) {

            var k = 0

            override fun getParent(node: Any?): Any? {

                val result: Any? = when (k) {
                    0 -> Unit
                    1 -> notHandledNavigator
                    else -> null
                }
                k++
                return result
            }
        }

        dispatcher.dispatch(testMessage)
    }
}