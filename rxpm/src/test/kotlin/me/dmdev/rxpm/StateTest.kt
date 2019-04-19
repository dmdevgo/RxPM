package me.dmdev.rxpm

import com.jakewharton.rxrelay2.*
import com.nhaarman.mockitokotlin2.*
import me.dmdev.rxpm.PresentationModel.Lifecycle.*
import me.dmdev.rxpm.test.*
import me.dmdev.rxpm.util.*
import org.junit.*

class StateTest {

    @get:Rule
    val schedulers = SchedulersRule()

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