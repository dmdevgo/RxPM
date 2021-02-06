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

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.observers.TestObserver
import me.dmdev.rxpm.PresentationModel.Lifecycle
import me.dmdev.rxpm.PresentationModel.Lifecycle.*
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PresentationModelTest {

    private lateinit var lifecycleCallbacks: LifecycleCallbacks
    private lateinit var pm: TestPm
    private lateinit var lifecycleObserver: TestObserver<Lifecycle>

    @Before fun setUp() {
        lifecycleCallbacks = mock()
        pm = TestPm(lifecycleCallbacks)
        lifecycleObserver = pm.lifecycleObservable.test()
    }

    @Test fun observingLifecycle() {
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(UNBINDED)
        pm.lifecycleConsumer.accept(DESTROYED)

        lifecycleObserver.assertValuesOnly(CREATED, BINDED, UNBINDED, DESTROYED)
    }

    @Test fun invokeLifecycleCallbacks() {
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(UNBINDED)
        pm.lifecycleConsumer.accept(DESTROYED)

        verify(lifecycleCallbacks).onCreate()
        verify(lifecycleCallbacks).onBind()
        verify(lifecycleCallbacks).onUnbind()
        verify(lifecycleCallbacks).onDestroy()
    }

    @Test fun invokeLifecycleCallbacksWhenChildAttached() {
        val childPm = spy<PresentationModel>()
        childPm.attachToParent(pm)

        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(UNBINDED)
        pm.lifecycleConsumer.accept(DESTROYED)

        verify(lifecycleCallbacks).onCreate()
        verify(lifecycleCallbacks).onBind()
        verify(lifecycleCallbacks).onUnbind()
        verify(lifecycleCallbacks).onDestroy()
    }

    @Test fun currentLifecycleValue() {
        assertNull(pm.currentLifecycleState)

        pm.lifecycleConsumer.accept(CREATED)
        assertEquals(CREATED, pm.currentLifecycleState)

        pm.lifecycleConsumer.accept(BINDED)
        assertEquals(BINDED, pm.currentLifecycleState)

        pm.lifecycleConsumer.accept(UNBINDED)
        assertEquals(UNBINDED, pm.currentLifecycleState)

        pm.lifecycleConsumer.accept(DESTROYED)
        assertEquals(DESTROYED, pm.currentLifecycleState)
    }

    @Test fun commandBlocksItemsBeforeCreated() {
        val testObserver = pm.commands.observable.test()

        pm.acceptCommand(1)

        testObserver.assertEmpty()
    }

    @Test fun commandBlocksItemsBeforeBinded() {
        val testObserver = pm.commands.observable.test()

        pm.lifecycleConsumer.accept(CREATED)
        pm.acceptCommand(1)

        testObserver.assertEmpty()
    }

    @Test fun commandPassItemsWhenBinded() {
        val testObserver = pm.commands.observable.test()

        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.acceptCommand(1)
        pm.acceptCommand(2)

        testObserver.assertValuesOnly(1, 2)
    }

    @Test fun commandSendsBufferedItemsAfterBinded() {
        val testObserver = pm.commands.observable.test()

        pm.acceptCommand(1)
        pm.lifecycleConsumer.accept(CREATED)
        pm.acceptCommand(2)
        pm.lifecycleConsumer.accept(BINDED)

        testObserver.assertValuesOnly(1, 2)
    }

    @Test fun commandBlocksItemsAfterUnbinded() {
        val testObserver = pm.commands.observable.test()

        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(UNBINDED)
        pm.acceptCommand(1)

        testObserver.assertEmpty()
    }

    @Test fun commandSendsBufferedItemsAfterBindedAgain() {
        val testObserver = pm.commands.observable.test()

        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(UNBINDED)
        pm.acceptCommand(1)
        pm.acceptCommand(2)
        pm.lifecycleConsumer.accept(BINDED)

        testObserver.assertValuesOnly(1, 2)
    }

    @Test fun commandBlocksItemsAfterDestroyed() {
        val testObserver = pm.commands.observable.test()

        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(UNBINDED)
        pm.lifecycleConsumer.accept(DESTROYED)
        pm.acceptCommand(1)

        testObserver.assertEmpty()
    }

    @Test fun commandSendsBufferedItemsAfterResubscribedAndBindedAgain() {
        val testObserver = pm.commands.observable.test()

        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(UNBINDED)
        testObserver.dispose()

        pm.acceptCommand(1)

        val testObserver2 = pm.commands.observable.test()
        pm.lifecycleConsumer.accept(BINDED)

        testObserver.assertEmpty()
        testObserver2.assertValuesOnly(1)
    }
}

open class TestPm(private val callbacks: LifecycleCallbacks) : PresentationModel() {

    val commands = Command<Int>()

    fun acceptCommand(i : Int) {
        commands.consumer.accept(i)
    }

    override fun onCreate() {
        callbacks.onCreate()
    }

    override fun onBind() {
        callbacks.onBind()
    }

    override fun onUnbind() {
        callbacks.onUnbind()
    }

    override fun onDestroy() {
        callbacks.onDestroy()
    }
}

interface LifecycleCallbacks {
    fun onCreate() {}
    fun onBind() {}
    fun onUnbind() {}
    fun onDestroy() {}
}