/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2021 Dmitriy Gorbunov (dmitriy.goto@gmail.com)
 *                     and Vasili Chyrvon (vasili.chyrvon@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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