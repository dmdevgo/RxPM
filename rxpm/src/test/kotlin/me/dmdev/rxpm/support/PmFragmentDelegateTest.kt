package me.dmdev.rxpm.support

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.TestObserver
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.android.support.PmFragmentDelegate
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

/**
 * @author Dmitriy Gorbunov
 */
class PmFragmentDelegateTest {

    lateinit var pm: PresentationModel
    lateinit var compositeDisposableMock: CompositeDisposable
    lateinit var pmViewMock: PmView<PresentationModel>
    lateinit var activityMock: FragmentActivity
    lateinit var fragmentMock: Fragment

    @Before
    fun initTest() {
        pm = spy<PresentationModel>()
        compositeDisposableMock = mock<CompositeDisposable>()

        pmViewMock = mock<PmView<PresentationModel>>()
        whenever(pmViewMock.compositeDisposable).thenReturn(compositeDisposableMock)
        whenever(pmViewMock.providePresentationModel()).thenReturn(pm)

        activityMock = mock<FragmentActivity>()
        fragmentMock = mock<Fragment>()
        whenever(fragmentMock.activity).thenReturn(activityMock)
    }

    @Test
    fun testViewLifeCycle() {

        val delegate = PmFragmentDelegate(fragmentMock, pmViewMock)

        delegate.onCreate(null)

        verify(pmViewMock).providePresentationModel()
        assertEquals(pm, delegate.pm)

        delegate.onStart()
        verify(pmViewMock).onBindPresentationModel()

        delegate.onResume()
        delegate.onPause()

        delegate.onStop()

        verify(pmViewMock).onUnbindPresentationModel()
        verify(compositeDisposableMock).clear()

        whenever(activityMock.isFinishing).thenReturn(true)
        whenever(fragmentMock.isRemoving).thenReturn(true)
        delegate.onDestroy()
    }

    @Test
    fun testPresentationModelLifeCycle() {

        val testObserver = TestObserver<PresentationModel.Lifecycle>()
        pm.lifecycleState.subscribe(testObserver)

        val delegate = PmFragmentDelegate(fragmentMock, pmViewMock)

        delegate.onCreate(null)
        delegate.onStart()
        delegate.onResume()
        delegate.onPause()
        delegate.onStop()
        whenever(activityMock.isFinishing).thenReturn(true)
        whenever(fragmentMock.isRemoving).thenReturn(true)
        delegate.onDestroy()

        testObserver.assertValues(PresentationModel.Lifecycle.CREATED,
                                  PresentationModel.Lifecycle.BINDED,
                                  PresentationModel.Lifecycle.UNBINDED,
                                  PresentationModel.Lifecycle.DESTROYED)
    }

}