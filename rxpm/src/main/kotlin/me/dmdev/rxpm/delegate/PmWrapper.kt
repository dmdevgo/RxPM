package me.dmdev.rxpm.delegate

import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.Lifecycle
import me.jeevuz.outlast.Outlasting

internal class PmWrapper<out PM : PresentationModel>(val pm: PM) : Outlasting {

    override fun onCreate() {
        pm.lifecycleConsumer.accept(Lifecycle.CREATED)
    }

    override fun onDestroy() {
        pm.lifecycleConsumer.accept(Lifecycle.DESTROYED)
    }
}