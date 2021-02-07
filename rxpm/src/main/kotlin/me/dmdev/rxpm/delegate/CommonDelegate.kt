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

import android.os.Bundle
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.Lifecycle
import me.dmdev.rxpm.bindTo
import me.dmdev.rxpm.navigation.NavigationMessageDispatcher
import me.dmdev.rxpm.navigation.NavigationalPm
import java.util.*

/**
 *  Common delegate serves for forwarding the lifecycle[PresentationModel.Lifecycle] directly into the [PresentationModel][PresentationModel].
 *  Can be used to implement your own delegate for the View[PmView].
 *
 *  @see PmActivityDelegate
 *  @see PmFragmentDelegate
 *  @see PmControllerDelegate
 */
class CommonDelegate<PM, V>(
    private val pmView: PmView<PM>,
    private val navigationMessagesDispatcher: NavigationMessageDispatcher
)
        where PM : PresentationModel,
              V : PmView<PM> {

    companion object {
        private const val SAVED_PM_TAG_KEY = "_rxpm_presentation_model_tag"
    }

    private lateinit var pmTag: String

    val presentationModel: PM by lazy(LazyThreadSafetyMode.NONE) {
        @Suppress("UNCHECKED_CAST")
        PmStore.getPm(pmTag) { pmView.providePresentationModel() } as PM
    }

    fun onCreate(savedInstanceState: Bundle?) {
        pmTag = savedInstanceState?.getString(SAVED_PM_TAG_KEY) ?: UUID.randomUUID().toString()
        if (presentationModel.currentLifecycleState == null) {
            presentationModel.lifecycleConsumer.accept(Lifecycle.CREATED)
        }
    }

    fun onBind() {

        val pm = presentationModel

        if (pm.currentLifecycleState == Lifecycle.CREATED
            || pm.currentLifecycleState == Lifecycle.UNBINDED
        ) {
            pm.lifecycleConsumer.accept(Lifecycle.BINDED)
            pmView.onBindPresentationModel(pm)

            if (pm is NavigationalPm) {
                pm.navigationMessages bindTo {
                    navigationMessagesDispatcher.dispatch(it)
                }
            }
        }
    }

    fun onResume() {
        if (presentationModel.currentLifecycleState == Lifecycle.BINDED
            || presentationModel.currentLifecycleState == Lifecycle.PAUSED
        ) {
            presentationModel.lifecycleConsumer.accept(Lifecycle.RESUMED)
        }
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SAVED_PM_TAG_KEY, pmTag)
    }

    fun onPause() {
        if (presentationModel.currentLifecycleState == Lifecycle.RESUMED) {
            presentationModel.lifecycleConsumer.accept(Lifecycle.PAUSED)
        }
    }

    fun onUnbind() {
        if (presentationModel.currentLifecycleState == Lifecycle.PAUSED
            || presentationModel.currentLifecycleState == Lifecycle.BINDED
        ) {
            pmView.onUnbindPresentationModel()
            presentationModel.lifecycleConsumer.accept(Lifecycle.UNBINDED)
        }
    }

    fun onDestroy() {
        if (presentationModel.currentLifecycleState == Lifecycle.CREATED
            || presentationModel.currentLifecycleState == Lifecycle.UNBINDED
        ) {
            PmStore.removePm(pmTag)
            presentationModel.lifecycleConsumer.accept(Lifecycle.DESTROYED)
        }
    }
}