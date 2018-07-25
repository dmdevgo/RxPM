package me.dmdev.rxpm.delegate

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.disposables.CompositeDisposable
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.base.PmController
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class PmControllerDelegateTest {

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
            on { compositeUnbind } doReturn compositeDisposable
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
        verify(compositeDisposable).clear()

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

        testObserver.assertValues(
            PresentationModel.Lifecycle.CREATED,
            PresentationModel.Lifecycle.BINDED,
            PresentationModel.Lifecycle.UNBINDED,
            PresentationModel.Lifecycle.DESTROYED
        )
    }
}