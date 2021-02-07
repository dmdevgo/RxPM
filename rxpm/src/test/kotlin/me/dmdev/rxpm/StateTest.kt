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

import com.jakewharton.rxrelay2.PublishRelay
import com.nhaarman.mockitokotlin2.spy
import me.dmdev.rxpm.PresentationModel.Lifecycle.*
import me.dmdev.rxpm.test.PmTestHelper
import me.dmdev.rxpm.util.SchedulersRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StateTest {

    @get:Rule val schedulers = SchedulersRule()

    private lateinit var pm: PresentationModel
    private lateinit var pmTestHelper: PmTestHelper

    @Before fun setUp() {
        pm = spy()
        pmTestHelper = PmTestHelper(pm)
    }

    @Test fun defaultDiffStrategyTheSameContent() {

        val state = pm.state<String>()
        val testObserver = state.observable.test()

        state.relay.accept("foo")
        state.relay.accept("foo")

        testObserver.assertValuesOnly("foo")
    }

    @Test fun defaultDiffStrategyContentIsDifferent() {

        val state = pm.state<String>()
        val testObserver = state.observable.test()

        state.relay.accept("foo")
        state.relay.accept("bar")

        testObserver.assertValuesOnly("foo", "bar")
    }


    @Test fun withoutDiffStrategy() {

        val state = pm.state<String>(diffStrategy = null)
        val testObserver = state.observable.test()

        state.relay.accept("foo")
        state.relay.accept("foo")

        testObserver.assertValuesOnly("foo", "foo")
    }

    @Test fun customDiffStrategy() {

        val state = pm.state(diffStrategy = object : DiffStrategy<String> {

            override fun areTheSame(new: String, old: String): Boolean {
                return if (new == "foo") {
                    false
                } else {
                    new == old
                }
            }

            override fun computeAsync() = true

        })

        val testObserver = state.observable.test()

        state.relay.accept("foo")
        state.relay.accept("foo")
        state.relay.accept("bar")
        state.relay.accept("bar")
        state.relay.accept("baz")
        state.relay.accept("baz")

        testObserver.assertValuesOnly("foo", "foo", "bar", "baz")
    }

    @Test fun blocksUpdatesBeforeCreated() {
        val state = pm.state("foo")
        val relay = PublishRelay.create<String>()
        val testObserver = relay.test()

        state.bindTo(relay)

        testObserver.assertEmpty()

    }

    @Test fun blocksUpdatesBeforeBinded() {
        val state = pm.state("foo")
        val relay = PublishRelay.create<String>()
        val testObserver = relay.test()

        state.bindTo(relay)

        pmTestHelper.setLifecycleTo(CREATED)

        testObserver.assertEmpty()
    }

    @Test fun blocksUpdatesBeforeResumed() {
        val state = pm.state("foo")
        val relay = PublishRelay.create<String>()
        val testObserver = relay.test()

        state.bindTo(relay)

        pmTestHelper.setLifecycleTo(BINDED)

        testObserver.assertNoValues()
    }

    @Test fun blocksUpdatesAfterPaused() {
        val state = pm.state<String>()
        val relay = PublishRelay.create<String>()
        val testObserver = relay.test()

        state.bindTo(relay)

        pmTestHelper.setLifecycleTo(PAUSED)

        state.relay.accept("foo")

        testObserver.assertNoValues()

    }

    @Test fun updatesWhenResumed() {
        val state = pm.state<String>()
        val relay = PublishRelay.create<String>()
        val testObserver = relay.test()

        state.bindTo(relay)

        pmTestHelper.setLifecycleTo(RESUMED)

        state.relay.accept("foo")
        state.relay.accept("bar")

        testObserver.assertValuesOnly("foo", "bar")
    }

    @Test fun sendBufferedValueAfterResumed() {
        val state = pm.state<String>()
        val relay = PublishRelay.create<String>()
        val testObserver = relay.test()

        state.bindTo(relay)

        state.relay.accept("foo")
        state.relay.accept("bar")

        pmTestHelper.setLifecycleTo(RESUMED)

        testObserver.assertValuesOnly("bar")
    }

    @Test fun sendBufferedValueAfterResumedAgain() {
        val state = pm.state<String>()
        val relay = PublishRelay.create<String>()
        val testObserver = relay.test()

        state.bindTo(relay)

        pmTestHelper.setLifecycleTo(PAUSED)

        state.relay.accept("foo")
        state.relay.accept("bar")

        pmTestHelper.setLifecycleTo(RESUMED)

        testObserver.assertValuesOnly("bar")

    }
}