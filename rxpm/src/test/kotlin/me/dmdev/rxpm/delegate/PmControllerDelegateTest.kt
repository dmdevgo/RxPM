package me.dmdev.rxpm.delegate

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.TestObserver
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class PmControllerDelegateTest {

    @Spy lateinit var pm: PresentationModel
    @Mock lateinit var compositeDisposableMock: CompositeDisposable
    @Mock lateinit var pmViewMock: PmView<PresentationModel>

    @Before
    fun initTest() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(pmViewMock.compositeUnbind).thenReturn(compositeDisposableMock)
        Mockito.`when`(pmViewMock.providePresentationModel()).thenReturn(pm)
    }

    @Test
    fun testViewLifeCycle() {

        val delegate = PmControllerDelegate(pmViewMock)

        delegate.onCreateView()
        verify(pmViewMock).providePresentationModel()
        assertEquals(pm, delegate.presentationModel)

        delegate.onAttach()
        verify(pmViewMock).onBindPresentationModel(pm)

        delegate.onDetach()
        verify(pmViewMock).onUnbindPresentationModel()
        verify(compositeDisposableMock).clear()

        delegate.onDestroyView()

        delegate.onDestroy()

    }

    @Test
    fun testPresentationModelLifeCycle() {

        val testObserver = TestObserver<PresentationModel.Lifecycle>()
        pm.lifecycleState.subscribe(testObserver)

        val delegate = PmControllerDelegate(pmViewMock)

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