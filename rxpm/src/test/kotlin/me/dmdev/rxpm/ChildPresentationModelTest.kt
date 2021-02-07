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

package me.dmdev.rxpm

import com.nhaarman.mockitokotlin2.spy
import io.reactivex.observers.TestObserver
import me.dmdev.rxpm.PresentationModel.Lifecycle
import me.dmdev.rxpm.PresentationModel.Lifecycle.*
import me.dmdev.rxpm.navigation.NavigationMessage
import me.dmdev.rxpm.navigation.NavigationalPm
import me.dmdev.rxpm.util.SchedulersRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertFailsWith

class ChildPresentationModelTest {

    @get:Rule val schedulers = SchedulersRule()

    private class ScreenPm : PresentationModel(), NavigationalPm {
        override val navigationMessages = command<NavigationMessage>()
    }

    private lateinit var pm: ScreenPm
    private lateinit var childPm: ScreenPm
    private lateinit var lifecycleObserver: TestObserver<Lifecycle>

    @Before fun setUp() {
        pm = spy()
        childPm = spy()
        lifecycleObserver = childPm.lifecycleObservable.test()
    }

    @Test fun attachToParent() {
        childPm.attachToParent(pm)
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)
        pm.lifecycleConsumer.accept(PAUSED)
        pm.lifecycleConsumer.accept(UNBINDED)
        pm.lifecycleConsumer.accept(DESTROYED)

        lifecycleObserver.assertValuesOnly(CREATED, BINDED, RESUMED, PAUSED, UNBINDED, DESTROYED)
    }

    @Test fun detachFromParent() {
        childPm.attachToParent(pm)
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)
        childPm.detachFromParent()

        lifecycleObserver.assertValuesOnly(CREATED, BINDED, RESUMED, PAUSED, UNBINDED, DESTROYED)
    }

    @Test fun attachToParentAfterCreated() {
        pm.lifecycleConsumer.accept(CREATED)
        childPm.attachToParent(pm)

        lifecycleObserver.assertValuesOnly(CREATED)
    }

    @Test fun attachToParentAfterBinded() {
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        childPm.attachToParent(pm)

        lifecycleObserver.assertValuesOnly(CREATED, BINDED)
    }

    @Test fun attachToParentAfterResumed() {
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)
        childPm.attachToParent(pm)

        lifecycleObserver.assertValuesOnly(CREATED, BINDED, RESUMED)
    }

    @Test fun attachToParentAfterPaused() {
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)
        pm.lifecycleConsumer.accept(PAUSED)
        childPm.attachToParent(pm)

        lifecycleObserver.assertValuesOnly(CREATED, BINDED)
    }

    @Test fun attachToParentAfterUnbinded() {
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)
        pm.lifecycleConsumer.accept(PAUSED)
        pm.lifecycleConsumer.accept(UNBINDED)
        childPm.attachToParent(pm)

        lifecycleObserver.assertValuesOnly(CREATED)
    }

    @Test fun throwOnAttachToParentAfterDestroyed() {
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)
        pm.lifecycleConsumer.accept(PAUSED)
        pm.lifecycleConsumer.accept(UNBINDED)
        pm.lifecycleConsumer.accept(DESTROYED)

        assertFailsWith<IllegalStateException> {
            childPm.attachToParent(pm)
        }
    }

    @Test fun throwOnAttachToItself() {
        assertFailsWith<IllegalArgumentException> {
            childPm.attachToParent(childPm)
        }
    }

    @Test fun throwOnChildPmReuse() {
        childPm.attachToParent(pm)
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)
        childPm.detachFromParent()

        assertFailsWith<IllegalStateException> {
            childPm.attachToParent(pm)
        }
    }

    @Test fun passNavigationMessagesToParent() {
        val testMessage = object : NavigationMessage {}
        val testObserver = pm.navigationMessages.observable.test()
        childPm.attachToParent(pm)
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)

        childPm.navigationMessages.relay.accept(testMessage)

        testObserver.assertValuesOnly(testMessage)
    }

}