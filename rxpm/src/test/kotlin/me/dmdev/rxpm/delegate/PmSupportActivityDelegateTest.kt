package me.dmdev.rxpm.delegate

import com.nhaarman.mockitokotlin2.*
import io.reactivex.disposables.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.PresentationModel.Lifecycle.*
import me.dmdev.rxpm.base.*
import me.dmdev.rxpm.util.*
import org.junit.*
import org.junit.Test
import kotlin.test.*

class PmSupportActivityDelegateTest {

    @get:Rule val schedulers = SchedulersRule()

    private lateinit var pm: PresentationModel
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var view: PmSupportActivity<PresentationModel>
    private lateinit var delegate: PmActivityDelegate<PresentationModel, PmSupportActivity<PresentationModel>>

    @Before fun setUp() {
        pm = spy()
        compositeDisposable = mock()
        view = mockView()

        delegate = PmActivityDelegate(view)
    }

    private fun mockView(): PmSupportActivity<PresentationModel> {
        return mock {
            on { providePresentationModel() } doReturn pm
        }
    }

    @Test fun callViewMethods() {
        delegate.onCreate(null)

        verify(view).providePresentationModel()
        assertEquals(pm, delegate.presentationModel)

        delegate.onStart()
        verify(view).onBindPresentationModel(pm)

        delegate.onResume()
        delegate.onPause()

        delegate.onStop()

        verify(view).onUnbindPresentationModel()

        delegate.onDestroy()
    }

    @Test fun changePmLifecycle() {
        val testObserver = pm.lifecycleObservable.test()

        delegate.onCreate(null)
        delegate.onStart()
        delegate.onResume()
        delegate.onPause()
        delegate.onStop()
        whenever(view.isFinishing).thenReturn(true)
        delegate.onDestroy()

        testObserver.assertValuesOnly(
            CREATED,
            BINDED,
            UNBINDED,
            DESTROYED
        )
    }

}