package me.dmdev.rxpm.delegate

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.base.PmActivity
import me.dmdev.rxpm.navigation.ActivityNavigationMessageDispatcher

/**
 * Delegate for the [Activity] that helps with creation and binding of
 * a [presentation model][PresentationModel] and a [view][PmView].
 *
 * Use this class only if you can't subclass the [PmActivity].
 *
 * Users of this class must forward all the life cycle methods from the containing Activity
 * to the corresponding ones in this class.
 */
class PmActivityDelegate<PM, A>(private val pmActivity: A)
        where PM : PresentationModel,
              A : FragmentActivity, A : PmView<PM> {

    private val pmHolder: PmHolder by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(pmActivity, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PmHolder(pmActivity.providePresentationModel()) as T
            }

        }).get(PmHolder::class.java)
    }

    internal val pmBinder: PmBinder<PM> by lazy(LazyThreadSafetyMode.NONE) {
        PmBinder(presentationModel, pmActivity, ActivityNavigationMessageDispatcher(pmActivity))
    }

    val presentationModel: PM by lazy(LazyThreadSafetyMode.NONE) {
        @Suppress("UNCHECKED_CAST")
        pmHolder.pm as PM
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onCreate(savedInstanceState: Bundle?) {
        presentationModel // lazy initialization
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onStart() {
        pmBinder.bind()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onResume() {
        pmBinder.bind()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onSaveInstanceState(outState: Bundle) {
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
        // For symmetry, may be used in the future
    }
}