package me.dmdev.rxpm.delegate

import com.nhaarman.mockitokotlin2.*
import io.reactivex.disposables.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.PresentationModel.Lifecycle.*
import me.dmdev.rxpm.base.*
import me.dmdev.rxpm.delegate.PmActivityDelegate.*
import me.dmdev.rxpm.util.*
import org.junit.*
import org.junit.Test
import kotlin.test.*

class PmActivityDelegateTest {

    @get:Rule val schedulers = SchedulersRule()

    private lateinit var pm: PresentationModel
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var view: PmActivity<PresentationModel>
    private lateinit var delegate: PmActivityDelegate<PresentationModel, PmActivity<PresentationModel>>

    @Before fun setUp() {
        pm = spy()
        compositeDisposable = mock()
        view = mockView()

        delegate = PmActivityDelegate(view, RetainMode.IS_FINISHING)
    }

    private fun mockView(): PmActivity<PresentationModel> {
        return mock {
            on { providePresentationModel() } doReturn pm
        }
    }

    @Test fun callViewMethods() {
        delegate.onCreate(null)
        delegate.onPostCreate()

        verify(view).providePresentationModel()
        assertEquals(pm, delegate.presentationModel)
        verify(view).onBindPresentationModel(pm)

        delegate.onStart()
        delegate.onResume()
        delegate.onPause()
        delegate.onStop()

        delegate.onDestroy()
        verify(view).onUnbindPresentationModel()

    }

    @Test fun changePmLifecycle() {
        val testObserver = pm.lifecycleObservable.test()

        delegate.onCreate(null)
        delegate.onPostCreate()
        delegate.onStart()
        delegate.onResume()
        delegate.onPause()
        delegate.onStop()
        whenever(view.isFinishing).thenReturn(true)
        delegate.onDestroy()

        testObserver.assertValuesOnly(
            CREATED,
            BINDED,
            RESUMED,
            PAUSED,
            UNBINDED,
            DESTROYED
        )
    }

}