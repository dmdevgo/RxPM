package me.dmdev.rxpm

import com.nhaarman.mockitokotlin2.spy
import me.dmdev.rxpm.util.SchedulersRule
import org.junit.Rule
import org.junit.Test

class StateTest {

    @get:Rule
    val schedulers = SchedulersRule()

    @Test fun defaultDiffStrategyTheSameContent() {

        val pm = spy<PresentationModel>()
        val state = pm.state<String>()

        val testObserver = state.observable.test()

        state.relay.accept("foo")
        state.relay.accept("foo")

        testObserver.assertValuesOnly("foo")
    }

    @Test fun defaultDiffStrategyContentIsDifferent() {

        val pm = spy<PresentationModel>()
        val state = pm.state<String>()

        val testObserver = state.observable.test()

        state.relay.accept("foo")
        state.relay.accept("bar")

        testObserver.assertValuesOnly("foo", "bar")
    }


    @Test fun withoutDiffStrategy() {

        val pm = spy<PresentationModel>()
        val state = pm.state<String>(diffStrategy = null)

        val testObserver = state.observable.test()

        state.relay.accept("foo")
        state.relay.accept("foo")

        testObserver.assertValuesOnly("foo", "foo")
    }

    @Test fun customDiffStrategy() {

        val pm = spy<PresentationModel>()
        val state = pm.state(diffStrategy = object : DiffStrategy<String> {

            override fun isTheSame(new: String, old: String): Boolean {
                return if (new == "foo") {
                    false
                } else {
                    new == old
                }
            }

            override fun isAsync() = false

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
}