package me.dmdev.rxpm.android.support

import android.os.Bundle
import android.support.v4.app.Fragment
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.Lifecycle
import me.jeevuz.outlast.Outlasting
import me.jeevuz.outlast.predefined.FragmentOutlast

/**
 * @author Dmitriy Gorbunov
 */
class PmFragmentDelegate<out PM : PresentationModel>(private val fragment: Fragment,
                                                     private val pmView: PmView<PM>) {


    private lateinit var outlastDelegate: FragmentOutlast<PmWrapper<PM>>
    private var binded = false

    val pm: PM get() = outlastDelegate.outlasting.pm

    fun onCreate(savedInstanceState: Bundle?) {
        outlastDelegate = FragmentOutlast(fragment,
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
            pm.lifeCycleConsumer.accept(Lifecycle.ON_BIND)
            binded = true
        }
    }

    private fun unbind() {
        if (binded) {
            pm.lifeCycleConsumer.accept(Lifecycle.ON_UNBIND)
            pmView.onUnbindPresentationModel()
            pmView.compositeDisposable.clear()
            binded = false
        }
    }
}
