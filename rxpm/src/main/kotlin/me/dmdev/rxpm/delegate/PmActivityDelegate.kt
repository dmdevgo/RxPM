package me.dmdev.rxpm.delegate

import android.app.Activity
import android.os.Bundle
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.Lifecycle
import me.jeevuz.outlast.Outlasting
import me.jeevuz.outlast.predefined.ActivityOutlast

/**
 * @author Dmitriy Gorbunov
 */
class PmActivityDelegate<out PM : PresentationModel>(private val activity: Activity,
                                                     private val pmView: PmView<PM>) {

    private lateinit var outlastDelegate: ActivityOutlast<PmWrapper<PM>>
    private var binded = false
    val pm: PM get() = outlastDelegate.outlasting.pm

    fun onCreate(savedInstanceState: Bundle?) {
        outlastDelegate = ActivityOutlast(activity,
                                          Outlasting.Creator<PmWrapper<PM>> {
                                              PmWrapper(pmView.providePresentationModel())
                                          },
                                          savedInstanceState)
        outlastDelegate.outlasting.pm // D>- create outlasting object
    }

    fun onStart() {
        outlastDelegate.onStart()
        bind()
    }

    fun onResume() {
        outlastDelegate.onResume()
        bind()
    }

    fun onSaveInstanceState(outState: Bundle) {
        outlastDelegate.onSaveInstanceState(outState)
        unbind()
    }

    fun onPause() {
        //For symmetry, may be used in the future
    }

    fun onStop() {
        unbind()
    }

    fun onDestroy() {
        outlastDelegate.onDestroy()
    }

    private fun bind() {
        if (!binded) {
            pmView.onBindPresentationModel()
            pm.lifecycleConsumer.accept(Lifecycle.BINDED)
            binded = true
        }
    }

    private fun unbind() {
        if (binded) {
            pm.lifecycleConsumer.accept(Lifecycle.UNBINDED)
            pmView.onUnbindPresentationModel()
            pmView.compositeDisposable.clear()
            binded = false
        }
    }

}