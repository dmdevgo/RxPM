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

import com.bluelinelabs.conductor.Controller
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.Lifecycle
import me.dmdev.rxpm.base.PmController
import me.dmdev.rxpm.navigation.ControllerNavigationMessageDispatcher

/**
 * Delegate for the [Controller] that helps with creation and binding of
 * a [presentation model][PresentationModel] and a [view][PmView].
 *
 * Use this class only if you can't subclass the [PmController].
 *
 * Users of this class must forward all the life cycle methods from the containing Controller
 * to the corresponding ones in this class.
 */
class PmControllerDelegate<PM, C>(private val pmView: C)
        where PM : PresentationModel,
              C : Controller, C : PmView<PM> {

    internal val pmBinder: PmBinder<PM> by lazy(LazyThreadSafetyMode.NONE) { PmBinder(presentationModel, pmView) }
    private var created = false

    private val navigationMessageDispatcher = ControllerNavigationMessageDispatcher(pmView)
    private var navigationMessagesDisposable: Disposable? = null

    val presentationModel: PM by lazy(LazyThreadSafetyMode.NONE) { pmView.providePresentationModel() }

    private fun onCreate() {
        presentationModel.lifecycleConsumer.accept(Lifecycle.CREATED)
        navigationMessagesDisposable = presentationModel.navigationMessages.observable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                navigationMessageDispatcher.dispatch(it)
            }
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onCreateView() {
        if (!created) {
            onCreate()
            created = true
        }
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onAttach() {
        pmBinder.bind()
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onDetach() {
        pmBinder.unbind()
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onDestroyView() {
        // May be used in the future
    }

    /**
     * You must call this method from the containing [Controller]'s corresponding method.
     */
    fun onDestroy() {
        if (created) {
            navigationMessagesDisposable?.dispose()
            presentationModel.lifecycleConsumer.accept(Lifecycle.DESTROYED)
        }
    }
}