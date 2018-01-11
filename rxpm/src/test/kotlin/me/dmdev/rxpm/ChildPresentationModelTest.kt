package me.dmdev.rxpm

import io.reactivex.observers.TestObserver
import me.dmdev.rxpm.PresentationModel.Lifecycle
import me.dmdev.rxpm.navigation.NavigationMessage
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class ChildPresentationModelTest {

    private lateinit var pm: PresentationModel
    private lateinit var childPm: PresentationModel
    private lateinit var to: TestObserver<Lifecycle>

    @Before
    fun init() {
        pm = Mockito.spy(PresentationModel::class.java)
        childPm = Mockito.spy(PresentationModel::class.java)
        to = TestObserver()
        childPm.lifecycleObservable.subscribe(to)
    }

    private fun checkLifecycle() {
        to.assertSubscribed()
        to.assertValues(Lifecycle.CREATED,
                        Lifecycle.BINDED,
                        Lifecycle.UNBINDED,
                        Lifecycle.DESTROYED)
        to.assertNoErrors()
    }

    @Test
    fun testChildAttachToParent() {

        childPm.attachToParent(pm)

        pm.lifecycleConsumer.accept(Lifecycle.CREATED)
        pm.lifecycleConsumer.accept(Lifecycle.BINDED)
        pm.lifecycleConsumer.accept(Lifecycle.UNBINDED)
        pm.lifecycleConsumer.accept(Lifecycle.DESTROYED)

        checkLifecycle()
    }

    @Test
    fun testChildDetachFromParent() {

        childPm.attachToParent(pm)

        pm.lifecycleConsumer.accept(Lifecycle.CREATED)
        pm.lifecycleConsumer.accept(Lifecycle.BINDED)

        childPm.detachFromParent()

        checkLifecycle()
    }

    @Test
    fun testAttachToParentAfterCreate() {

        pm.lifecycleConsumer.accept(Lifecycle.CREATED)
        childPm.attachToParent(pm)
        pm.lifecycleConsumer.accept(Lifecycle.BINDED)
        pm.lifecycleConsumer.accept(Lifecycle.UNBINDED)
        pm.lifecycleConsumer.accept(Lifecycle.DESTROYED)

        checkLifecycle()

    }

    @Test
    fun testAttachToParentAfterBind() {

        pm.lifecycleConsumer.accept(Lifecycle.CREATED)
        pm.lifecycleConsumer.accept(Lifecycle.BINDED)
        childPm.attachToParent(pm)
        pm.lifecycleConsumer.accept(Lifecycle.UNBINDED)
        pm.lifecycleConsumer.accept(Lifecycle.DESTROYED)

        checkLifecycle()

    }

    @Test
    fun testAttachToParentAfterUnbind() {

        pm.lifecycleConsumer.accept(Lifecycle.CREATED)
        pm.lifecycleConsumer.accept(Lifecycle.BINDED)
        pm.lifecycleConsumer.accept(Lifecycle.UNBINDED)
        childPm.attachToParent(pm)
        pm.lifecycleConsumer.accept(Lifecycle.DESTROYED)

        to.assertSubscribed()
        to.assertValues(Lifecycle.CREATED,
                        Lifecycle.DESTROYED)
        to.assertNoErrors()

    }

    @Test(expected = IllegalStateException::class)
    fun testAttachToParentAfterDestroy() {

        pm.lifecycleConsumer.accept(Lifecycle.CREATED)
        pm.lifecycleConsumer.accept(Lifecycle.BINDED)
        pm.lifecycleConsumer.accept(Lifecycle.UNBINDED)
        pm.lifecycleConsumer.accept(Lifecycle.DESTROYED)

        childPm.attachToParent(pm)

    }

    @Test(expected = IllegalArgumentException::class)
    fun testAttachToParentItself() {
        childPm.attachToParent(childPm)
    }

    @Test(expected = IllegalStateException::class)
    fun testChildPmReusing() {

        childPm.attachToParent(pm)

        pm.lifecycleConsumer.accept(Lifecycle.CREATED)
        pm.lifecycleConsumer.accept(Lifecycle.BINDED)

        childPm.detachFromParent()

        childPm.attachToParent(pm)

    }

    @Test
    fun testMessageSending() {
        val testMessage = Mockito.mock(NavigationMessage::class.java)
        val to = TestObserver<NavigationMessage>()

        pm.navigationMessages.observable.subscribe(to)

        childPm.attachToParent(pm)
        pm.lifecycleConsumer.accept(Lifecycle.CREATED)
        pm.lifecycleConsumer.accept(Lifecycle.BINDED)

        childPm.navigationMessages.relay.accept(testMessage)

        to.assertSubscribed()
        to.assertValues(testMessage)
        to.assertNoErrors()

    }

}