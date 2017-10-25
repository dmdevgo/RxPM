package me.dmdev.rxpm.delegate

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.TestObserver
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.base.PmSupportActivity
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
class PmSupportActivityDelegateTest {

    @Spy lateinit var pm: PresentationModel
    @Mock lateinit var compositeDisposableMock: CompositeDisposable
    @Mock lateinit var activityMock: PmSupportActivity<PresentationModel>

    @Before
    fun initTest() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(activityMock.compositeUnbind).thenReturn(compositeDisposableMock)
        Mockito.`when`(activityMock.providePresentationModel()).thenReturn(pm)
    }

    @Test
    fun testViewLifeCycle() {

        val delegate = PmActivityDelegate(activityMock)

        delegate.onCreate(null)

        verify(activityMock).providePresentationModel()
        assertEquals(pm, delegate.presentationModel)

        delegate.onStart()
        verify(activityMock).onBindPresentationModel(pm)

        delegate.onResume()
        delegate.onPause()

        delegate.onStop()

        verify(activityMock).onUnbindPresentationModel()
        verify(compositeDisposableMock).clear()

        Mockito.`when`(activityMock.isFinishing).thenReturn(true)
        delegate.onDestroy()
    }

    @Test
    fun testPresentationModelLifeCycle() {

        val testObserver = TestObserver<PresentationModel.Lifecycle>()
        pm.lifecycleState.subscribe(testObserver)

        val delegate = PmActivityDelegate(activityMock)

        delegate.onCreate(null)
        delegate.onStart()
        delegate.onResume()
        delegate.onPause()
        delegate.onStop()
        Mockito.`when`(activityMock.isFinishing).thenReturn(true)
        delegate.onDestroy()

        testObserver.assertValues(PresentationModel.Lifecycle.CREATED,
                                  PresentationModel.Lifecycle.BINDED,
                                  PresentationModel.Lifecycle.UNBINDED,
                                  PresentationModel.Lifecycle.DESTROYED)
    }

}