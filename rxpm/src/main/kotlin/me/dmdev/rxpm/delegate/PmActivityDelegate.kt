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

import android.app.Activity
import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.base.PmSupportActivity
import me.dmdev.rxpm.navigation.ActivityNavigationMessageDispatcher
import me.jeevuz.outlast.Outlasting
import me.jeevuz.outlast.predefined.ActivityOutlast

/**
 * Delegate for the [Activity] that helps with creation and binding of
 * a [presentation model][PresentationModel] and a [view][PmView].
 *
 * Use this class only if you can't subclass the [PmSupportActivity].
 *
 * Users of this class must forward all the life cycle methods from the containing Activity
 * to the corresponding ones in this class.
 */
class PmActivityDelegate<PM, A>(private val pmView: A)
        where PM : PresentationModel,
              A : Activity, A : PmView<PM> {

    private lateinit var outlast: ActivityOutlast<PmWrapper<PM>>
    internal lateinit var pmBinder: PmBinder<PM>

    private lateinit var navigationMessagesDisposable: Disposable
    private val navigationMessagesDispatcher = ActivityNavigationMessageDispatcher(pmView)

    val presentationModel: PM by lazy(LazyThreadSafetyMode.NONE) { outlast.outlasting.presentationModel }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onCreate(savedInstanceState: Bundle?) {
        outlast = ActivityOutlast(
            pmView,
            Outlasting.Creator<PmWrapper<PM>> {
                PmWrapper(pmView.providePresentationModel())
            },
            savedInstanceState
        )
        presentationModel // Create lazy presentation model now
        pmBinder = PmBinder(presentationModel, pmView)
        navigationMessagesDisposable = presentationModel.navigationMessages.observable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                navigationMessagesDispatcher.dispatch(it)
            }
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onStart() {
        outlast.onStart()
        pmBinder.bind()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onResume() {
        outlast.onResume()
        pmBinder.bind()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onSaveInstanceState(outState: Bundle) {
        outlast.onSaveInstanceState(outState)
        pmBinder.unbind()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onPause() {
        // For symmetry, may be used in the future
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onStop() {
        pmBinder.unbind()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onDestroy() {
        navigationMessagesDisposable.dispose()
        outlast.onDestroy()
    }
}