package me.dmdev.rxpm.delegate

import android.os.*
import androidx.fragment.app.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.base.*
import me.dmdev.rxpm.delegate.PmFragmentDelegate.RetainMode.*
import me.dmdev.rxpm.navigation.*

/**
 * Delegate for the [Fragment] that helps with creation and binding of
 * a [presentation model][PresentationModel] and a [view][PmView].
 *
 * Use this class only if you can't subclass the [PmFragment].
 *
 * Users of this class must forward all the lifecycle methods from the containing Fragment
 * to the corresponding ones in this class.
 */
class PmFragmentDelegate<PM, F>(
    private val pmFragment: F,
    private val retainMode: RetainMode
)
        where PM : PresentationModel,
              F : Fragment, F : PmView<PM> {

    /**
     * Strategies for retaining the PresentationModel[PresentationModel].
     * [SAVED_STATE] - the PresentationModel will be destroyed if the Activity is finishing or the Fragment state has not been saved.
     * [CONFIGURATION_CHANGES] - Retain the PresentationModel during a configuration change.
     */
    enum class RetainMode { SAVED_STATE, CONFIGURATION_CHANGES }

    private val commonDelegate = CommonDelegate<PM, F>(pmFragment, FragmentNavigationMessageDispatcher(pmFragment))

    val presentationModel: PM get() = commonDelegate.presentationModel

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onCreate(savedInstanceState: Bundle?) {
        commonDelegate.onCreate(savedInstanceState)
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onViewCreated() {
        commonDelegate.onBind()
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onStart() {
        // For symmetry, may be used in the future
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onResume() {
        commonDelegate.onResume()
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onSaveInstanceState(outState: Bundle) {
        commonDelegate.onSaveInstanceState(outState)
        commonDelegate.onPause()
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        commonDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onPause() {
        commonDelegate.onPause()
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onStop() {
        // For symmetry, may be used in the future
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onDestroyView() {
        commonDelegate.onUnbind()
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onDestroy() {
        when (retainMode) {
            SAVED_STATE -> {
                if (pmFragment.activity?.isFinishing == true
                    || (pmFragment.fragmentManager?.isStateSaved?.not() == true)
                ) {
                    commonDelegate.onDestroy()
                }
            }

            CONFIGURATION_CHANGES -> {
                if (pmFragment.activity?.isChangingConfigurations?.not() == true) {
                    commonDelegate.onDestroy()
                }
            }
        }
    }
}
