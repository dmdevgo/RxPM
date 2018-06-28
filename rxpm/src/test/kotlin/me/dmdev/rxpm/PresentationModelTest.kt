package me.dmdev.rxpm

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.observers.TestObserver
import me.dmdev.rxpm.PresentationModel.Lifecycle
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class PresentationModelTest {

    @Test
    fun testLifeCycle() {

        val callbacks = Mockito.mock(LifeCycleCallbacks::class.java)
        val testPm = TestPm(callbacks)
        val to = TestObserver<Lifecycle>()

        testPm.lifecycleObservable.subscribe(to)

        testPm.lifecycleConsumer.accept(Lifecycle.CREATED)
        testPm.lifecycleConsumer.accept(Lifecycle.BINDED)
        testPm.lifecycleConsumer.accept(Lifecycle.UNBINDED)
        testPm.lifecycleConsumer.accept(Lifecycle.DESTROYED)

        to.assertSubscribed()
        to.assertValues(Lifecycle.CREATED,
                        Lifecycle.BINDED,
                        Lifecycle.UNBINDED,
                        Lifecycle.DESTROYED)
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
        val pm = TestPmWithChild(childPm)
        val to = TestObserver<Lifecycle>()

        pm.lifecycleObservable.subscribe(to)

        pm.lifecycleConsumer.accept(Lifecycle.CREATED)
        pm.lifecycleConsumer.accept(Lifecycle.BINDED)
        pm.lifecycleConsumer.accept(Lifecycle.UNBINDED)
        pm.lifecycleConsumer.accept(Lifecycle.DESTROYED)

        to.assertSubscribed()
        to.assertValues(Lifecycle.CREATED,
                        Lifecycle.BINDED,
                        Lifecycle.UNBINDED,
                        Lifecycle.DESTROYED)
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

        pm.relay.accept(1)

        pm.lifecycleConsumer.accept(Lifecycle.CREATED)

        pm.relay.accept(2)

        Assert.assertArrayEquals(intArrayOf(), commands.toIntArray())

        pm.lifecycleConsumer.accept(Lifecycle.BINDED)

        Assert.assertArrayEquals(intArrayOf(1, 2), commands.toIntArray())

        pm.relay.accept(3)
        pm.relay.accept(4)

        Assert.assertArrayEquals(intArrayOf(1, 2, 3, 4), commands.toIntArray())

        pm.lifecycleConsumer.accept(Lifecycle.UNBINDED)

        pm.relay.accept(5)
        pm.relay.accept(6)

        Assert.assertArrayEquals(intArrayOf(1, 2, 3, 4), commands.toIntArray())

        pm.lifecycleConsumer.accept(Lifecycle.BINDED)

        Assert.assertArrayEquals(intArrayOf(1, 2, 3, 4, 5, 6), commands.toIntArray())

        pm.lifecycleConsumer.accept(Lifecycle.UNBINDED)

        pm.relay.accept(7)

        Assert.assertArrayEquals(intArrayOf(1, 2, 3, 4, 5, 6), commands.toIntArray())

        pm.lifecycleConsumer.accept(Lifecycle.DESTROYED)

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

        pm.lifecycleConsumer.accept(Lifecycle.CREATED)

        pm.relay.accept(1)
        pm.relay.accept(2)
        pm.relay.accept(3)

        Assert.assertArrayEquals(intArrayOf(), commands.toIntArray())

        pm.lifecycleConsumer.accept(Lifecycle.BINDED)

        Assert.assertArrayEquals(intArrayOf(3), commands.toIntArray())

    }
}

open class TestPmWithChild(private val childPm: TestPm) : PresentationModel() {

    override fun onCreate() {
        super.onCreate()
        childPm.attachToParent(this)
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