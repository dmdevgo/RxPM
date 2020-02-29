package me.dmdev.rxpm.delegate

import androidx.fragment.app.*
import com.nhaarman.mockitokotlin2.*
import io.reactivex.disposables.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.PresentationModel.Lifecycle.*
import me.dmdev.rxpm.base.*
import me.dmdev.rxpm.delegate.PmFragmentDelegate.*
import me.dmdev.rxpm.util.*
import org.junit.*
import org.junit.Test
import kotlin.test.*

class PmFragmentDelegateTest {

    @get:Rule val schedulers = SchedulersRule()

    private lateinit var pm: PresentationModel
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var activity: FragmentActivity
    private lateinit var view: PmFragment<PresentationModel>
    private lateinit var delegate: PmFragmentDelegate<PresentationModel, PmFragment<PresentationModel>>

    @Before fun setUp() {
        pm = spy()
        compositeDisposable = mock()
        activity = mock()
        view = mockView()

        delegate = PmFragmentDelegate(view, RetainMode.CONFIGURATION_CHANGES)
    }

    private fun mockView(): PmFragment<PresentationModel> {
        return mock {
            on { providePresentationModel() } doReturn pm
            on { activity } doReturn activity
        }
    }

    @Test fun callViewMethods() {

        delegate.onCreate(null)
        delegate.onViewCreated(null)
        delegate.onActivityCreated(null)

        verify(view).providePresentationModel()
        assertEquals(pm, delegate.presentationModel)
        verify(view).onBindPresentationModel(pm)

        delegate.onStart()
        delegate.onResume()
        delegate.onPause()
        delegate.onStop()

        delegate.onDestroyView()
        verify(view).onUnbindPresentationModel()
        delegate.onDestroy()
    }

    @Test fun changePmLifecycle() {

        val testObserver = pm.lifecycleObservable.test()

        delegate.onCreate(null)
        delegate.onViewCreated(null)
        delegate.onActivityCreated(null)
        delegate.onStart()
        delegate.onResume()
        delegate.onPause()
        delegate.onStop()
        delegate.onDestroyView()
        whenever(activity.isFinishing).thenReturn(true)
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