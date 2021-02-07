/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2021 Dmitriy Gorbunov (dmitriy.goto@gmail.com)
 *                     and Vasili Chyrvon (vasili.chyrvon@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.dmdev.rxpm.delegate

import androidx.fragment.app.FragmentActivity
import com.nhaarman.mockitokotlin2.*
import io.reactivex.disposables.CompositeDisposable
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.Lifecycle.*
import me.dmdev.rxpm.base.PmFragment
import me.dmdev.rxpm.delegate.PmFragmentDelegate.RetainMode
import me.dmdev.rxpm.util.SchedulersRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

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