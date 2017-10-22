package me.dmdev.rxpm.delegate

import android.support.v4.app.FragmentActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.TestObserver
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.base.PmSupportFragment
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

@RunWith(MockitoJUnitRunner.Silent::class)
class PmSupportFragmentDelegateTest {

    @Spy lateinit var pm: PresentationModel
    @Mock lateinit var compositeDisposableMock: CompositeDisposable
    @Mock lateinit var activityMock: FragmentActivity
    @Mock lateinit var fragmentMock: PmSupportFragment<PresentationModel>

    @Before
    fun initTest() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(fragmentMock.compositeUnbind).thenReturn(compositeDisposableMock)
        Mockito.`when`(fragmentMock.providePresentationModel()).thenReturn(pm)
        Mockito.`when`(fragmentMock.activity).thenReturn(activityMock)
    }

    @Test
    fun testViewLifeCycle() {

        val delegate = PmSupportFragmentDelegate(fragmentMock)

        delegate.onCreate(null)

        verify(fragmentMock).providePresentationModel()
        assertEquals(pm, delegate.presentationModel)

        delegate.onStart()
        verify(fragmentMock).onBindPresentationModel(pm)

        delegate.onResume()
        delegate.onPause()

        delegate.onStop()

        verify(fragmentMock).onUnbindPresentationModel()
        verify(compositeDisposableMock).clear()

        delegate.onDestroy()
    }

    @Test
    fun testPresentationModelLifeCycle() {

        val testObserver = TestObserver<PresentationModel.Lifecycle>()
        pm.lifecycleState.subscribe(testObserver)

        val delegate = PmSupportFragmentDelegate(fragmentMock)

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