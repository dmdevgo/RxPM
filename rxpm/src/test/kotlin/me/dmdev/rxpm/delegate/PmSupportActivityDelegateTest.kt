package me.dmdev.rxpm.delegate

import com.nhaarman.mockitokotlin2.*
import io.reactivex.disposables.CompositeDisposable
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.base.PmSupportActivity
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class PmSupportActivityDelegateTest {

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
            on { compositeUnbind } doReturn compositeDisposable
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
        verify(compositeDisposable).clear()

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

        testObserver.assertValues(
            PresentationModel.Lifecycle.CREATED,
            PresentationModel.Lifecycle.BINDED,
            PresentationModel.Lifecycle.UNBINDED,
            PresentationModel.Lifecycle.DESTROYED
        )
    }

}