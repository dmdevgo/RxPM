package me.dmdev.rxpm.delegate

import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel

/**
 * @author Dmitriy Gorbunov
 */
internal class PmBinder<out PM : PresentationModel>(private val pm: PM,
                                                    private val pmView: PmView<PM>) {

    var binded = false
        private set

    fun bind() {
        if (!binded) {
            pmView.onBindPresentationModel(pm)
            pm.lifecycleConsumer.accept(PresentationModel.Lifecycle.BINDED)
            binded = true
        }
    }

    fun unbind() {
        if (binded) {
            pm.lifecycleConsumer.accept(PresentationModel.Lifecycle.UNBINDED)
            pmView.onUnbindPresentationModel()
            pmView.compositeUnbind.clear()
            binded = false
        }
    }
}