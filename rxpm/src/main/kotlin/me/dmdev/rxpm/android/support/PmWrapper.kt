package me.dmdev.rxpm.android.support

import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.Lifecycle
import me.jeevuz.outlast.Outlasting

class PmWrapper<out PM : PresentationModel>(val pm: PM) : Outlasting {

    override fun onCreate() {
        pm.lifeCycleConsumer.accept(Lifecycle.ON_CREATE)
    }

    override fun onDestroy() {
        pm.lifeCycleConsumer.accept(Lifecycle.ON_DESTROY)
    }
}