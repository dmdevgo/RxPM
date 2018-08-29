package me.dmdev.rxpm

import com.jakewharton.rxrelay2.PublishRelay
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

    @Test fun bufferWhileUnbindBlockItemsBeforeCreated() {
        val testObserver = pm.commands.test()

        pm.relay.accept(1)

        testObserver.assertEmpty()
    }

    @Test fun bufferWhileUnbindBlockItemsBeforeBinded() {
        val testObserver = pm.commands.test()

        pm.lifecycleConsumer.accept(CREATED)
        pm.relay.accept(1)

        testObserver.assertEmpty()
    }

    @Test fun bufferWhileUnbindReceiveItemsWhenBinded() {
        val testObserver = pm.commands.test()

        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.relay.accept(1)
        pm.relay.accept(2)

        testObserver.assertValuesOnly(1, 2)
    }

    @Test fun bufferWhileUnbindPassItemsAfterBinded() {
        val testObserver = pm.commands.test()

        pm.relay.accept(1)
        pm.lifecycleConsumer.accept(CREATED)
        pm.relay.accept(2)
        pm.lifecycleConsumer.accept(BINDED)

        testObserver.assertValuesOnly(1, 2)
    }

    @Test fun bufferWhileUnbindBlockItemsAfterUnbinded() {
        val testObserver = pm.commands.test()

        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(UNBINDED)
        pm.relay.accept(1)

        testObserver.assertEmpty()
    }

    @Test fun bufferWhileUnbindPassItemsAfterBindedAgain() {
        val testObserver = pm.commands.test()

        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(UNBINDED)
        pm.relay.accept(1)
        pm.relay.accept(2)
        pm.lifecycleConsumer.accept(BINDED)

        testObserver.assertValuesOnly(1, 2)
    }

    @Test fun bufferWhileUnbindBlockItemsAfterDestroyed() {
        val testObserver = pm.commands.test()

        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(UNBINDED)
        pm.lifecycleConsumer.accept(DESTROYED)
        pm.relay.accept(1)

        testObserver.assertEmpty()
    }
}

open class TestPm(private val callbacks: LifecycleCallbacks) : PresentationModel() {

    val relay = PublishRelay.create<Int>()
    val commands = relay.bufferWhileUnbind()

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