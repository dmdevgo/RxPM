package me.dmdev.rxpm.delegate

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.*
import me.dmdev.rxpm.navigation.NavigationMessageDispatcher

internal class PmBinder<out PM : PresentationModel>(
    private val pmView: PmView<PM>,
    private val navigationMessageDispatcher: NavigationMessageDispatcher
) {

    private val pm get() = pmView.presentationModel

    var listener: Callbacks? = null

    private var navigationMessagesDisposable: Disposable? = null

    fun bind() {
        if (pm.currentLifecycleState == Lifecycle.CREATED || pm.currentLifecycleState == Lifecycle.UNBINDED) {

            navigationMessagesDisposable = pm.navigationMessages.observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    navigationMessageDispatcher.dispatch(it)
                }

            pmView.onBindPresentationModel(pm)
            listener?.onBindPm()
            pm.lifecycleConsumer.accept(Lifecycle.BINDED)
        }
    }

    fun unbind() {
        if (pm.currentLifecycleState == Lifecycle.BINDED) {
            pm.lifecycleConsumer.accept(Lifecycle.UNBINDED)
            listener?.onUnbindPm()
            pmView.onUnbindPresentationModel()
            pmView.compositeUnbind.clear()
            navigationMessagesDisposable?.dispose()
        }
    }

    internal interface Callbacks {
        fun onBindPm()
        fun onUnbindPm()
    }
}