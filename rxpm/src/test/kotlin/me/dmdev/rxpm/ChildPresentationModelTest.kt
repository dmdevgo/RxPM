package me.dmdev.rxpm

import com.nhaarman.mockitokotlin2.spy
import io.reactivex.observers.TestObserver
import me.dmdev.rxpm.PresentationModel.Lifecycle
import me.dmdev.rxpm.PresentationModel.Lifecycle.*
import me.dmdev.rxpm.navigation.NavigationMessage
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith

class ChildPresentationModelTest {

    private lateinit var pm: PresentationModel
    private lateinit var childPm: PresentationModel
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
        pm.lifecycleConsumer.accept(UNBINDED)
        pm.lifecycleConsumer.accept(DESTROYED)

        lifecycleObserver.assertValuesOnly(CREATED, BINDED, UNBINDED, DESTROYED)
    }

    @Test fun detachFromParent() {
        childPm.attachToParent(pm)
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        childPm.detachFromParent()

        lifecycleObserver.assertValuesOnly(CREATED, BINDED, UNBINDED, DESTROYED)
    }

    @Test fun attachToParentAfterCreated() {
        pm.lifecycleConsumer.accept(CREATED)
        childPm.attachToParent(pm)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(UNBINDED)
        pm.lifecycleConsumer.accept(DESTROYED)

        lifecycleObserver.assertValuesOnly(CREATED, BINDED, UNBINDED, DESTROYED)
    }

    @Test fun attachToParentAfterBinded() {
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        childPm.attachToParent(pm)
        pm.lifecycleConsumer.accept(UNBINDED)
        pm.lifecycleConsumer.accept(DESTROYED)

        lifecycleObserver.assertValuesOnly(CREATED, BINDED, UNBINDED, DESTROYED)
    }

    @Test fun attachToParentAfterUnbinded() {
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(UNBINDED)
        childPm.attachToParent(pm)
        pm.lifecycleConsumer.accept(DESTROYED)

        lifecycleObserver.assertValuesOnly(CREATED, DESTROYED)
    }

    @Test fun throwOnAttachToParentAfterDestroyed() {
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
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

        childPm.navigationMessages.relay.accept(testMessage)

        testObserver.assertValuesOnly(testMessage)
    }

}