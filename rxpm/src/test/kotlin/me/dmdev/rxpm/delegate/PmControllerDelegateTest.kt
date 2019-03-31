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

class PmControllerDelegateTest {

    @get:Rule val schedulers = SchedulersRule()

    private lateinit var pm: PresentationModel
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var view: PmController<PresentationModel>
    private lateinit var delegate: PmControllerDelegate<PresentationModel, PmController<PresentationModel>>

    @Before fun setUp() {
        pm = spy()
        compositeDisposable = mock()
        view = mockView()

        delegate = PmControllerDelegate(view)
    }

    private fun mockView(): PmController<PresentationModel> {
        return mock {
            on { providePresentationModel() } doReturn pm
        }
    }

    @Test fun callViewMethods() {
        delegate.onCreateView()

        verify(view).providePresentationModel()
        assertEquals(pm, delegate.presentationModel)

        delegate.onAttach()

        verify(view).onBindPresentationModel(pm)

        delegate.onDetach()

        verify(view).onUnbindPresentationModel()

        delegate.onDestroyView()
        delegate.onDestroy()
    }

    @Test fun changePmLifecycle() {
        val testObserver = pm.lifecycleObservable.test()

        delegate.onCreateView()
        delegate.onAttach()
        delegate.onDetach()
        delegate.onDestroyView()
        delegate.onDestroy()

        testObserver.assertValuesOnly(
            CREATED,
            BINDED,
            UNBINDED,
            DESTROYED
        )
    }
}