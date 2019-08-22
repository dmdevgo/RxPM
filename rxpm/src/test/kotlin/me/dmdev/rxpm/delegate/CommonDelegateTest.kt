package me.dmdev.rxpm.delegate

import com.nhaarman.mockitokotlin2.*
import io.reactivex.disposables.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.navigation.*
import me.dmdev.rxpm.util.*
import org.junit.*
import org.junit.Test
import kotlin.test.*

class CommonDelegateTest {

    @get:Rule val schedulers = SchedulersRule()

    private lateinit var pm: PresentationModel
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var view: PmView<PresentationModel>
    private lateinit var navigationMessagesDispatcher: NavigationMessageDispatcher
    private lateinit var delegate: CommonDelegate<PresentationModel, PmView<PresentationModel>>

    @Before fun setUp() {
        pm = spy()
        compositeDisposable = mock()
        view = mockView()
        navigationMessagesDispatcher = mock()
        delegate = CommonDelegate(view, navigationMessagesDispatcher)
    }

    private fun mockView(): PmView<PresentationModel> {
        return mock {
            on { providePresentationModel() } doReturn pm
        }
    }

    @Test fun callViewMethods() {

        verify(view, never()).providePresentationModel()
        delegate.onCreate(null)
        verify(view).providePresentationModel()
        assertEquals(pm, delegate.presentationModel)

        verify(view, never()).onBindPresentationModel(pm)
        delegate.onBind()
        verify(view).onBindPresentationModel(pm)

        delegate.onResume()
        delegate.onPause()

        verify(view, never()).onUnbindPresentationModel()
        delegate.onUnbind()
        verify(view).onUnbindPresentationModel()

        delegate.onDestroy()

        verify(view, times(1)).onBindPresentationModel(pm)
        verify(view, times(1)).onUnbindPresentationModel()
        verify(view, times(1)).onUnbindPresentationModel()
    }

    @Test fun changePmLifecycle() {

        val testObserver = pm.lifecycleObservable.test()

        delegate.onCreate(null)
        delegate.onBind()
        delegate.onResume()
        delegate.onPause()
        delegate.onUnbind()
        delegate.onDestroy()

        testObserver.assertValuesOnly(
            PresentationModel.Lifecycle.CREATED,
            PresentationModel.Lifecycle.BINDED,
            PresentationModel.Lifecycle.RESUMED,
            PresentationModel.Lifecycle.PAUSED,
            PresentationModel.Lifecycle.UNBINDED,
            PresentationModel.Lifecycle.DESTROYED
        )
    }

    @Test fun filterRepeatedLifecycleCalls() {

        val testObserver = pm.lifecycleObservable.test()

        delegate.onCreate(null)
        delegate.onCreate(null)
        delegate.onBind()
        delegate.onBind()
        delegate.onResume()
        delegate.onResume()
        delegate.onPause()
        delegate.onPause()
        delegate.onUnbind()
        delegate.onUnbind()
        delegate.onDestroy()
        delegate.onDestroy()

        testObserver.assertValuesOnly(
            PresentationModel.Lifecycle.CREATED,
            PresentationModel.Lifecycle.BINDED,
            PresentationModel.Lifecycle.RESUMED,
            PresentationModel.Lifecycle.PAUSED,
            PresentationModel.Lifecycle.UNBINDED,
            PresentationModel.Lifecycle.DESTROYED
        )
    }
}