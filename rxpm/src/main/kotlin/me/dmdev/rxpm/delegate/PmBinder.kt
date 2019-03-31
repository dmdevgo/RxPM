package me.dmdev.rxpm.delegate

import me.dmdev.rxpm.*

internal class PmBinder<out PM : PresentationModel>(
    private val pm: PM,
    private val pmView: PmView<PM>
) {

    var viewBound = false
        private set

    var listener: Callbacks? = null

    fun bind() {
        if (!viewBound) {
            pmView.onBindPresentationModel(pm)
            pm.lifecycleConsumer.accept(PresentationModel.Lifecycle.BINDED)
            viewBound = true
            listener?.onBindPm()
        }
    }

    fun unbind() {
        if (viewBound) {
            listener?.onUnbindPm()
            pm.lifecycleConsumer.accept(PresentationModel.Lifecycle.UNBINDED)
            pmView.onUnbindPresentationModel()
            viewBound = false
        }
    }

    internal interface Callbacks {
        fun onBindPm()
        fun onUnbindPm()
    }
}