package me.dmdev.rxpm.support

import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.LifeCycleState
import me.jeevuz.outlast.Outlasting

class PmWrapper<out PM : PresentationModel>(val pm: PM) : Outlasting {

    override fun onCreate() {
        pm.lifeCycleConsumer.accept(LifeCycleState.ON_CREATE)
    }

    override fun onDestroy() {
        pm.lifeCycleConsumer.accept(LifeCycleState.ON_DESTROY)
    }
}