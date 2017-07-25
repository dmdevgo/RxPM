package me.dmdev.rxpm

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.observers.TestObserver
import me.dmdev.rxpm.PresentationModel.Lifecycle
import org.junit.Assert
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
        val to = TestObserver<Lifecycle>()

        testPm.lifeCycleObservable.subscribe(to)

        testPm.lifeCycleConsumer.accept(Lifecycle.ON_CREATE)
        testPm.lifeCycleConsumer.accept(Lifecycle.ON_BIND)
        testPm.lifeCycleConsumer.accept(Lifecycle.ON_UNBIND)
        testPm.lifeCycleConsumer.accept(Lifecycle.ON_DESTROY)

        to.assertSubscribed()
        to.assertValues(Lifecycle.NULL,
                        Lifecycle.ON_CREATE,
                        Lifecycle.ON_BIND,
                        Lifecycle.ON_UNBIND,
                        Lifecycle.ON_DESTROY)
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
        val to = TestObserver<Lifecycle>()

        testChildPm.lifeCycleObservable.subscribe(to)

        testChildPm.lifeCycleConsumer.accept(Lifecycle.ON_CREATE)
        testChildPm.lifeCycleConsumer.accept(Lifecycle.ON_BIND)
        testChildPm.lifeCycleConsumer.accept(Lifecycle.ON_UNBIND)
        testChildPm.lifeCycleConsumer.accept(Lifecycle.ON_DESTROY)

        to.assertSubscribed()
        to.assertValues(Lifecycle.NULL,
                        Lifecycle.ON_CREATE,
                        Lifecycle.ON_BIND,
                        Lifecycle.ON_UNBIND,
                        Lifecycle.ON_DESTROY)
        to.assertNoErrors()

        Mockito.verify(callbacks).onCreate()
        Mockito.verify(callbacks).onBind()
        Mockito.verify(callbacks).onUnbind()
        Mockito.verify(callbacks).onDestroy()

    }

    @Test
    fun testBufferWhileUnbind() {
        val pm = object : PresentationModel() {
            val relay = PublishRelay.create<Int>()
            val commands = relay.bufferWhileUnbind()
        }

        val commands = mutableListOf<Int>()

        pm.commands.subscribe { commands.add(it) }

        pm.lifeCycleConsumer.accept(Lifecycle.ON_CREATE)

        pm.relay.accept(1)
        pm.relay.accept(2)

        Assert.assertArrayEquals(intArrayOf(), commands.toIntArray())

        pm.lifeCycleConsumer.accept(Lifecycle.ON_BIND)

        Assert.assertArrayEquals(intArrayOf(1, 2), commands.toIntArray())

        pm.relay.accept(3)
        pm.relay.accept(4)

        Assert.assertArrayEquals(intArrayOf(1, 2, 3, 4), commands.toIntArray())

        pm.lifeCycleConsumer.accept(Lifecycle.ON_UNBIND)

        pm.relay.accept(5)
        pm.relay.accept(6)

        Assert.assertArrayEquals(intArrayOf(1, 2, 3, 4), commands.toIntArray())

        pm.lifeCycleConsumer.accept(Lifecycle.ON_BIND)

        Assert.assertArrayEquals(intArrayOf(1, 2, 3, 4, 5, 6), commands.toIntArray())

        pm.lifeCycleConsumer.accept(Lifecycle.ON_UNBIND)

        pm.relay.accept(7)

        Assert.assertArrayEquals(intArrayOf(1, 2, 3, 4, 5, 6), commands.toIntArray())

        pm.lifeCycleConsumer.accept(Lifecycle.ON_DESTROY)

        pm.relay.accept(8)

        Assert.assertArrayEquals(intArrayOf(1, 2, 3, 4, 5, 6), commands.toIntArray())

    }

    @Test
    fun testBufferWhileUnbindSize1() {
        val pm = object : PresentationModel() {
            val relay = PublishRelay.create<Int>()
            val commands = relay.bufferWhileUnbind(bufferSize = 1)
        }

        val commands = mutableListOf<Int>()

        pm.commands.subscribe { commands.add(it) }

        pm.lifeCycleConsumer.accept(Lifecycle.ON_CREATE)

        pm.relay.accept(1)
        pm.relay.accept(2)
        pm.relay.accept(3)

        Assert.assertArrayEquals(intArrayOf(), commands.toIntArray())

        pm.lifeCycleConsumer.accept(Lifecycle.ON_BIND)

        Assert.assertArrayEquals(intArrayOf(3), commands.toIntArray())

    }
}

open class TestChildPm(childPm: TestPm) : PresentationModel() {
    init {
        childPm.bindLifecycle()
    }
}

open class TestPm(val callbacks: LifeCycleCallbacks) : PresentationModel() {
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

open class LifeCycleCallbacks {
    open fun onCreate() {}
    open fun onBind() {}
    open fun onUnbind() {}
    open fun onDestroy() {}
}