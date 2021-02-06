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

package me.dmdev.rxpm.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RestoreViewOnCreateController
import io.reactivex.disposables.CompositeDisposable
import me.dmdev.rxpm.AndroidPmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.delegate.PmControllerDelegate

/**
 * Predefined [Conductor's Controller][RestoreViewOnCreateController] implementing the [PmView][AndroidPmView].
 *
 * Just override the [providePresentationModel] and [onBindPresentationModel] methods and you are good to go.
 *
 * If extending is not possible you can implement [AndroidPmView],
 * create a [PmControllerDelegate] and pass the lifecycle callbacks to it.
 * See this class's source code for the example.
 */
abstract class PmController<PM : PresentationModel>(args: Bundle? = null) :
    RestoreViewOnCreateController(args),
    AndroidPmView<PM> {

    private val delegate by lazy(LazyThreadSafetyMode.NONE) { PmControllerDelegate(this) }

    final override val compositeUnbind = CompositeDisposable()

    final override val presentationModel get() = delegate.presentationModel

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        val view = createView(inflater, container, savedViewState)
        delegate.onCreateView()
        return view
    }

    /**
     * Replaces the [onCreateView] that the library hides for internal use.
     */
    abstract fun createView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View

    override fun onAttach(view: View) {
        super.onAttach(view)
        delegate.onAttach()
    }

    override fun onDetach(view: View) {
        super.onDetach(view)
        delegate.onDetach()
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        delegate.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        delegate.onDestroy()
    }
}