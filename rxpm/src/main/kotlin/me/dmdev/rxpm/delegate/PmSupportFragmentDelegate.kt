package me.dmdev.rxpm.delegate

import android.os.Bundle
import android.support.v4.app.Fragment
import io.reactivex.disposables.Disposable
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.base.PmSupportFragment
import me.dmdev.rxpm.navigation.SupportFragmentNavigationMessageDispatcher
import me.jeevuz.outlast.Outlasting
import me.jeevuz.outlast.predefined.FragmentOutlast

/**
 * Delegate for the [Fragment] that helps with creation and binding of
 * a [presentation model][PresentationModel] and a [view][PmView].
 *
 * Use this class only if you can't subclass the [PmSupportFragment].
 *
 * Users of this class must forward all the life cycle methods from the containing Fragment
 * to the corresponding ones in this class.
 */
class PmSupportFragmentDelegate<PM, F>(private val pmView: F)
where PM : PresentationModel, F : Fragment, F : PmView<PM> {

    private lateinit var outlast: FragmentOutlast<PmWrapper<PM>>
    internal lateinit var pmBinder: PmBinder<PM>

    private lateinit var navigationMessagesDisposable: Disposable
    private val navigationMessageDispatcher = SupportFragmentNavigationMessageDispatcher(pmView)

    val presentationModel: PM by lazy { outlast.outlasting.presentationModel }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onCreate(savedInstanceState: Bundle?) {
        outlast = FragmentOutlast(pmView,
                                  Outlasting.Creator<PmWrapper<PM>> {
                                      PmWrapper(pmView.providePresentationModel())
                                  },
                                  savedInstanceState)
        presentationModel // Create lazy presentation model now
        pmBinder = PmBinder(presentationModel, pmView)
        navigationMessagesDisposable = presentationModel.navigationMessages.observable.subscribe {
            navigationMessageDispatcher.dispatch(it)
        }
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onStart() {
        outlast.onStart()
        pmBinder.bind()
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onResume() {
        outlast.onResume()
        pmBinder.bind()
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onSaveInstanceState(outState: Bundle) {
        outlast.onSaveInstanceState(outState)
        pmBinder.unbind()
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onPause() {
        // For symmetry, may be used in the future
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onStop() {
        pmBinder.unbind()
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onDestroy() {
        navigationMessagesDisposable.dispose()
        outlast.onDestroy()
    }
}
