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

import com.nhaarman.mockitokotlin2.*
import io.reactivex.disposables.CompositeDisposable
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.Lifecycle.*
import me.dmdev.rxpm.base.PmActivity
import me.dmdev.rxpm.delegate.PmActivityDelegate.RetainMode
import me.dmdev.rxpm.util.SchedulersRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class PmActivityDelegateTest {

    @get:Rule val schedulers = SchedulersRule()

    private lateinit var pm: PresentationModel
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var view: PmActivity<PresentationModel>
    private lateinit var delegate: PmActivityDelegate<PresentationModel, PmActivity<PresentationModel>>

    @Before fun setUp() {
        pm = spy()
        compositeDisposable = mock()
        view = mockView()

        delegate = PmActivityDelegate(view, RetainMode.IS_FINISHING)
    }

    private fun mockView(): PmActivity<PresentationModel> {
        return mock {
            on { providePresentationModel() } doReturn pm
        }
    }

    @Test fun callViewMethods() {
        delegate.onCreate(null)
        delegate.onPostCreate()

        verify(view).providePresentationModel()
        assertEquals(pm, delegate.presentationModel)
        verify(view).onBindPresentationModel(pm)

        delegate.onStart()
        delegate.onResume()
        delegate.onPause()
        delegate.onStop()

        delegate.onDestroy()
        verify(view).onUnbindPresentationModel()

    }

    @Test fun changePmLifecycle() {
        val testObserver = pm.lifecycleObservable.test()

        delegate.onCreate(null)
        delegate.onPostCreate()
        delegate.onStart()
        delegate.onResume()
        delegate.onPause()
        delegate.onStop()
        whenever(view.isFinishing).thenReturn(true)
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