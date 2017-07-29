package me.dmdev.rxpm.delegate

import android.app.Activity
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.TestObserver
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.base.PmSupportActivity
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals


/**
 * @author Dmitriy Gorbunov
 */
class PmSupportActivityDelegateTest { // TODO: Rename this class

    lateinit var pm: PresentationModel
    lateinit var compositeDisposableMock: CompositeDisposable
    lateinit var pmViewMock: PmView<PresentationModel>
    lateinit var activityMock: Activity

    @Before
    fun initTest() {
        pm = spy<PresentationModel>()
        compositeDisposableMock = mock<CompositeDisposable>()

        pmViewMock = mock<PmSupportActivity<PresentationModel>>()
        whenever(pmViewMock.compositeUnbind).thenReturn(compositeDisposableMock)
        whenever(pmViewMock.providePresentationModel()).thenReturn(pm)

        activityMock = pmViewMock as Activity
    }

    @Test
    fun testViewLifeCycle() {

        val delegate = PmActivityOrFragmentDelegate(pmViewMock)

        delegate.onCreate(null)

        verify(pmViewMock).providePresentationModel()
        assertEquals(pm, delegate.presentationModel)

        delegate.onStart()
        verify(pmViewMock).onBindPresentationModel(pm)

        delegate.onResume()
        delegate.onPause()

        delegate.onStop()

        verify(pmViewMock).onUnbindPresentationModel()
        verify(compositeDisposableMock).clear()

        whenever(activityMock.isFinishing).thenReturn(true)
        delegate.onDestroy()
    }

    @Test
    fun testPresentationModelLifeCycle() {

        val testObserver = TestObserver<PresentationModel.Lifecycle>()
        pm.lifecycleState.subscribe(testObserver)

        val delegate = PmActivityOrFragmentDelegate(pmViewMock)

        delegate.onCreate(null)
        delegate.onStart()
        delegate.onResume()
        delegate.onPause()
        delegate.onStop()
        whenever(activityMock.isFinishing).thenReturn(true)
        delegate.onDestroy()

        testObserver.assertValues(PresentationModel.Lifecycle.CREATED,
                                  PresentationModel.Lifecycle.BINDED,
                                  PresentationModel.Lifecycle.UNBINDED,
                                  PresentationModel.Lifecycle.DESTROYED)
    }

}