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