package me.dmdev.rxpm.delegate

import android.support.v4.app.*
import com.nhaarman.mockitokotlin2.*
import io.reactivex.disposables.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.PresentationModel.Lifecycle.*
import me.dmdev.rxpm.base.*
import me.dmdev.rxpm.util.*
import org.junit.*
import org.junit.Test
import kotlin.test.*

class PmSupportFragmentDelegateTest {

    @get:Rule val schedulers = SchedulersRule()

    private lateinit var pm: PresentationModel
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var activity: FragmentActivity
    private lateinit var view: PmSupportFragment<PresentationModel>
    private lateinit var delegate: PmSupportFragmentDelegate<PresentationModel, PmSupportFragment<PresentationModel>>

    @Before fun setUp() {
        pm = spy()
        compositeDisposable = mock()
        activity = mock()
        view = mockView()

        delegate = PmSupportFragmentDelegate(view)
    }

    private fun mockView(): PmSupportFragment<PresentationModel> {
        return mock {
            on { providePresentationModel() } doReturn pm
            on { activity } doReturn activity
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
        whenever(activity.isFinishing).thenReturn(true)
        delegate.onDestroy()

        testObserver.assertValuesOnly(
            CREATED,
            BINDED,
            UNBINDED,
            DESTROYED
        )
    }

}