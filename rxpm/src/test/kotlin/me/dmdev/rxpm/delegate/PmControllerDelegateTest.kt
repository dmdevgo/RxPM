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

import android.view.View
import com.bluelinelabs.conductor.Controller
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.disposables.CompositeDisposable
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.Lifecycle.*
import me.dmdev.rxpm.base.PmController
import me.dmdev.rxpm.util.SchedulersRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import kotlin.test.assertEquals


class PmControllerDelegateTest {

    @get:Rule val schedulers = SchedulersRule()

    private lateinit var pm: PresentationModel
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var pmController: PmController<PresentationModel>
    private lateinit var view: View
    private lateinit var delegate: PmControllerDelegate<PresentationModel, PmController<PresentationModel>>
    private lateinit var controllerLifecycleListener: Controller.LifecycleListener

    @Before fun setUp() {
        pm = spy()
        compositeDisposable = mock()
        pmController = mockPmController()
        view = mock()
        delegate = PmControllerDelegate(pmController)
        controllerLifecycleListener = captureControllerLifecycleListener()
    }

    private fun captureControllerLifecycleListener(): Controller.LifecycleListener {
        val argument = ArgumentCaptor.forClass(Controller.LifecycleListener::class.java)
        verify(pmController).addLifecycleListener(argument.capture())
        return argument.value
    }

    private fun mockPmController(): PmController<PresentationModel> {
        return mock {
            on { providePresentationModel() } doReturn pm
        }
    }

    @Test fun callViewMethods() {

        controllerLifecycleListener.preCreateView(pmController)

        verify(pmController).providePresentationModel()
        assertEquals(pm, delegate.presentationModel)

        controllerLifecycleListener.postCreateView(pmController, view)

        verify(pmController).onBindPresentationModel(pm)

        controllerLifecycleListener.preAttach(pmController, view)
        controllerLifecycleListener.postAttach(pmController, view)
        controllerLifecycleListener.preDetach(pmController, view)
        controllerLifecycleListener.postDetach(pmController, view)
        controllerLifecycleListener.preDestroyView(pmController, view)

        verify(pmController).onUnbindPresentationModel()

        controllerLifecycleListener.postDestroyView(pmController)
        controllerLifecycleListener.preDestroy(pmController)
        controllerLifecycleListener.postDestroy(pmController)

    }

    @Test fun changePmLifecycle() {
        val testObserver = pm.lifecycleObservable.test()

        controllerLifecycleListener.preCreateView(pmController)
        controllerLifecycleListener.postCreateView(pmController, view)
        controllerLifecycleListener.preAttach(pmController, view)
        controllerLifecycleListener.postAttach(pmController, view)
        controllerLifecycleListener.preDetach(pmController, view)
        controllerLifecycleListener.postDetach(pmController, view)
        controllerLifecycleListener.preDestroyView(pmController, view)
        controllerLifecycleListener.postDestroyView(pmController)
        controllerLifecycleListener.preDestroy(pmController)
        controllerLifecycleListener.postDestroy(pmController)

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