package me.dmdev.rxpm.delegate

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.TestObserver
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

/**
 * @author Dmitriy Gorbunov
 */
class PmControllerDelegateTest {

    lateinit var pm: PresentationModel
    lateinit var compositeDisposableMock: CompositeDisposable
    lateinit var pmViewMock: PmView<PresentationModel>

    @Before
    fun initTest() {
        pm = spy<PresentationModel>()
        compositeDisposableMock = mock<CompositeDisposable>()

        pmViewMock = mock<PmView<PresentationModel>>()
        whenever(pmViewMock.compositeUnbind).thenReturn(compositeDisposableMock)
        whenever(pmViewMock.providePresentationModel()).thenReturn(pm)
    }

    @Test
    fun testViewLifeCycle() {

        val delegate = PmControllerDelegate(pmViewMock)

        delegate.onCreate()

        verify(pmViewMock).providePresentationModel()
        assertEquals(pm, delegate.presentationModel)

        delegate.onCreateView()
        verify(pmViewMock).onBindPresentationModel(pm)

        delegate.onAttach()
        delegate.onDetach()

        delegate.onDestroyView()
        verify(pmViewMock).onUnbindPresentationModel()
        verify(compositeDisposableMock).clear()

        delegate.onDestroy()

    }

    @Test
    fun testPresentationModelLifeCycle() {

        val testObserver = TestObserver<PresentationModel.Lifecycle>()
        pm.lifecycleState.subscribe(testObserver)

        val delegate = PmControllerDelegate(pmViewMock)

        delegate.onCreate()
        delegate.onCreateView()
        delegate.onAttach()
        delegate.onDetach()
        delegate.onDestroyView()
        delegate.onDestroy()

        testObserver.assertValues(PresentationModel.Lifecycle.CREATED,
                                  PresentationModel.Lifecycle.BINDED,
                                  PresentationModel.Lifecycle.UNBINDED,
                                  PresentationModel.Lifecycle.DESTROYED)
    }
}