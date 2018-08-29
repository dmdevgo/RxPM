package me.dmdev.rxpm.test

import com.nhaarman.mockitokotlin2.spy
import io.reactivex.observers.TestObserver
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.Lifecycle.*
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith

class PmTestHelperTest {

    private lateinit var pmTestHelper: PmTestHelper
    private lateinit var lifecycleObserver: TestObserver<PresentationModel.Lifecycle>

    @Before fun setUp() {
        val pm = spy<PresentationModel>()
        lifecycleObserver = pm.lifecycleObservable.test()
        pmTestHelper = PmTestHelper(pm)
    }

    @Test fun initToCreated() {
        pmTestHelper.setLifecycleTo(CREATED)

        lifecycleObserver.assertValuesOnly(CREATED)
    }

    @Test fun initToBinded() {
        pmTestHelper.setLifecycleTo(BINDED)

        lifecycleObserver.assertValuesOnly(CREATED, BINDED)
    }

    @Test fun initToUnbinded() {
        pmTestHelper.setLifecycleTo(UNBINDED)

        lifecycleObserver.assertValuesOnly(CREATED, BINDED, UNBINDED)
    }

    @Test fun initToDestroyed() {
        pmTestHelper.setLifecycleTo(DESTROYED)

        lifecycleObserver.assertValuesOnly(CREATED, BINDED, UNBINDED, DESTROYED)
    }

    @Test fun initToDestroyedShort() {
        pmTestHelper.setLifecycleTo(DESTROYED, shortSequence = true)

        lifecycleObserver.assertValuesOnly(CREATED, DESTROYED)
    }

    @Test fun noShortForOthers() {
        // UNBINDED as an example
        pmTestHelper.setLifecycleTo(UNBINDED, shortSequence = true)

        lifecycleObserver.assertValuesOnly(CREATED, BINDED, UNBINDED)
    }

    @Test fun setOneByOne() {
        pmTestHelper.setLifecycleTo(CREATED)
        pmTestHelper.setLifecycleTo(BINDED)
        pmTestHelper.setLifecycleTo(UNBINDED)
        pmTestHelper.setLifecycleTo(DESTROYED)

        lifecycleObserver.assertValuesOnly(CREATED, BINDED, UNBINDED, DESTROYED)
    }

    @Test fun throwOnDuplicateState() {
        pmTestHelper.setLifecycleTo(CREATED)
        assertFailsWith<IllegalStateException>(
            "You can't set lifecycle state as CREATED when it already is CREATED."
        ) {
            pmTestHelper.setLifecycleTo(CREATED)
        }
    }

    @Test fun throwOnCreatedAfterBinded() {
        pmTestHelper.setLifecycleTo(BINDED)

        assertFailsWith<IllegalStateException>(
            "You can't set lifecycle state as CREATED when it already is BINDED."
        ) {
            pmTestHelper.setLifecycleTo(CREATED)
        }
    }

    @Test fun throwOnCreatedAfterUnbinded() {
        pmTestHelper.setLifecycleTo(UNBINDED)

        assertFailsWith<IllegalStateException>(
            "You can't set lifecycle state as CREATED when it already is UNBINDED."
        ) {
            pmTestHelper.setLifecycleTo(CREATED)
        }
    }

    @Test fun throwOnCreatedAfterDestroyed() {
        pmTestHelper.setLifecycleTo(DESTROYED)

        assertFailsWith<IllegalStateException>(
            "You can't set lifecycle state as CREATED when it already is DESTROYED."
        ) {
            pmTestHelper.setLifecycleTo(CREATED)
        }
    }

    @Test fun setBindedAfterUnbinded() {
        pmTestHelper.setLifecycleTo(UNBINDED)
        pmTestHelper.setLifecycleTo(BINDED)

        lifecycleObserver.assertValuesOnly(CREATED, BINDED, UNBINDED, BINDED)
    }

    @Test fun setMultipleBindedAndUnbinded() {
        pmTestHelper.setLifecycleTo(UNBINDED)
        pmTestHelper.setLifecycleTo(BINDED)
        pmTestHelper.setLifecycleTo(UNBINDED)
        pmTestHelper.setLifecycleTo(BINDED)
        pmTestHelper.setLifecycleTo(DESTROYED)

        lifecycleObserver.assertValuesOnly(
            CREATED,
            BINDED,
            UNBINDED,
            BINDED,
            UNBINDED,
            BINDED,
            UNBINDED,
            DESTROYED
        )
    }

    @Test fun throwOnBindedAfterDestroyed() {
        pmTestHelper.setLifecycleTo(DESTROYED)

        assertFailsWith<IllegalStateException>(
            "You can't set lifecycle state as BINDED when it already is DESTROYED."
        ) {
            pmTestHelper.setLifecycleTo(BINDED)
        }
    }

    @Test fun throwOnUnbindedAfterDestroyed() {
        pmTestHelper.setLifecycleTo(DESTROYED)

        assertFailsWith<IllegalStateException>(
            "You can't set lifecycle state as UNBINDED when it already is DESTROYED."
        ) {
            pmTestHelper.setLifecycleTo(UNBINDED)
        }
    }
}