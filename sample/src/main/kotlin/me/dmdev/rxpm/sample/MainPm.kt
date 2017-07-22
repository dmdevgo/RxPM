package me.dmdev.rxpm.sample

import me.dmdev.rxpm.PresentationModel
import timber.log.Timber

/**
 * @author Dmitriy Gorbunov
 */
class MainPm : PresentationModel() {

    private val childPm = ChildPm().bindLifecycle()

    override fun onCreate() {
        Timber.i("onCreate")
    }

    override fun onBind() {
        Timber.i("onBind")
    }

    override fun onUnbind() {
        Timber.i("onUnbind")
    }

    override fun onDestroy() {
        Timber.i("onDestroy")
    }

    class ChildPm : PresentationModel() {
        override fun onCreate() {
            Timber.i("onCreate")
        }

        override fun onBind() {
            Timber.i("onBind")
        }

        override fun onUnbind() {
            Timber.i("onUnbind")
        }

        override fun onDestroy() {
            Timber.i("onDestroy")
        }
    }
}