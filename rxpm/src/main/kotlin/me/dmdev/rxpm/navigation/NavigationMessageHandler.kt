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