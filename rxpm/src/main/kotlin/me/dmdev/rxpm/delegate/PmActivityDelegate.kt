package me.dmdev.rxpm.delegate

import android.app.Activity
import android.os.Bundle
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.base.PmSupportActivity
import me.jeevuz.outlast.Outlasting
import me.jeevuz.outlast.predefined.ActivityOutlast

/**
 * Delegate for the [Activity] that helps with creation and binding of
 * a [presentation model][PresentationModel] and a [view][PmView].
 *
 * Use this class only if you can't subclass the [PmSupportActivity].
 *
 * Users of this class must forward all the life cycle methods from the containing Activity
 * to the corresponding ones in this class.
 *
 * @author Dmitriy Gorbunov
 */
class PmActivityDelegate<PM : PresentationModel>(private val pmView: PmView<PM>) {

    init {
        require(pmView is Activity) {"This class can be used only with Activity PmView!"}
    }

    private lateinit var outlast: ActivityOutlast<PmWrapper<PM>>
    internal lateinit var pmBinder: PmBinder<PM>

    val presentationModel: PM by lazy { outlast.outlasting.presentationModel }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onCreate(savedInstanceState: Bundle?) {
        outlast = ActivityOutlast(pmView as Activity,
                                  Outlasting.Creator<PmWrapper<PM>> {
                                              PmWrapper(pmView.providePresentationModel())
                                          },
                                  savedInstanceState)
        presentationModel // Create lazy presentation model now
        pmBinder = PmBinder(presentationModel, pmView)
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onStart() {
        outlast.onStart()
        pmBinder.bind()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onResume() {
        outlast.onResume()
        pmBinder.bind()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onSaveInstanceState(outState: Bundle) {
        outlast.onSaveInstanceState(outState)
        pmBinder.unbind()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onPause() {
        // For symmetry, may be used in the future
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onStop() {
        pmBinder.unbind()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onDestroy() {
        outlast.onDestroy()
    }
}