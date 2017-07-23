package me.dmdev.rxpm

import io.reactivex.observers.TestObserver
import me.dmdev.rxpm.PresentationModel.LifeCycleState
import org.junit.Test
import org.mockito.Mockito

/**
 * @author Dmitriy Gorbunov
 */
class PresentationModelTest {

    @Test
    fun testLifeCycle() {

        val callbacks = Mockito.mock(LifeCycleCallbacks::class.java)
        val testPm = TestPm(callbacks)
        val to = TestObserver<LifeCycleState>()

        testPm.lifeCycleObservable.subscribe(to)

        testPm.lifeCycleConsumer.accept(LifeCycleState.ON_CREATE)
        testPm.lifeCycleConsumer.accept(LifeCycleState.ON_BIND)
        testPm.lifeCycleConsumer.accept(LifeCycleState.ON_UNBIND)
        testPm.lifeCycleConsumer.accept(LifeCycleState.ON_DESTROY)

        to.assertSubscribed()
        to.assertValues(LifeCycleState.NULL,
                        LifeCycleState.ON_CREATE,
                        LifeCycleState.ON_BIND,
                        LifeCycleState.ON_UNBIND,
                        LifeCycleState.ON_DESTROY)
        to.assertNoErrors()

        Mockito.verify(callbacks).onCreate()
        Mockito.verify(callbacks).onBind()
        Mockito.verify(callbacks).onUnbind()
        Mockito.verify(callbacks).onDestroy()

    }

    @Test
    fun testChildLifeCycle() {

        val callbacks = Mockito.mock(LifeCycleCallbacks::class.java)
        val childPm = TestPm(callbacks)
        val testChildPm = TestChildPm(childPm)
        val to = TestObserver<LifeCycleState>()

        testChildPm.lifeCycleObservable.subscribe(to)

        testChildPm.lifeCycleConsumer.accept(LifeCycleState.ON_CREATE)
        testChildPm.lifeCycleConsumer.accept(LifeCycleState.ON_BIND)
        testChildPm.lifeCycleConsumer.accept(LifeCycleState.ON_UNBIND)
        testChildPm.lifeCycleConsumer.accept(LifeCycleState.ON_DESTROY)

        to.assertSubscribed()
        to.assertValues(LifeCycleState.NULL,
                        LifeCycleState.ON_CREATE,
                        LifeCycleState.ON_BIND,
                        LifeCycleState.ON_UNBIND,
                        LifeCycleState.ON_DESTROY)
        to.assertNoErrors()

        Mockito.verify(callbacks).onCreate()
        Mockito.verify(callbacks).onBind()
        Mockito.verify(callbacks).onUnbind()
        Mockito.verify(callbacks).onDestroy()

    }
}

open class TestChildPm(childPm: TestPm) : PresentationModel() {
    init {
        childPm.bindLifecycle()
    }
}

open class TestPm(val callbacks: LifeCycleCallbacks) : PresentationModel() {
    override fun onCreate() { callbacks.onCreate() }
    override fun onBind() { callbacks.onBind() }
    override fun onUnbind() { callbacks.onUnbind() }
    override fun onDestroy() { callbacks.onDestroy() }
}

open class LifeCycleCallbacks {
    open fun onCreate() {}
    open fun onBind() {}
    open fun onUnbind() {}
    open fun onDestroy() {}
}