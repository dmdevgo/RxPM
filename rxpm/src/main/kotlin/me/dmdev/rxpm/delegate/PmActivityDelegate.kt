package me.dmdev.rxpm.delegate

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
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
class PmActivityDelegate<PM, A>(private val pmView: A)
        where PM : PresentationModel,
              A : FragmentActivity, A : PmView<PM> {


    private val pmHolder: PmHolder by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(pmView).get(PmHolder::class.java)
    }

    internal val pmBinder: PmBinder<PM> by lazy(LazyThreadSafetyMode.NONE) {
        PmBinder(presentationModel, pmView)
    }

    private val navigationMessagesDispatcher = ActivityNavigationMessageDispatcher(pmView)
    private lateinit var navigationMessagesDisposable: Disposable

    val presentationModel: PM by lazy(LazyThreadSafetyMode.NONE) {
        if (pmHolder.pm == null) {
            pmHolder.pm = pmView.providePresentationModel()
        }

        @Suppress("UNCHECKED_CAST")
        pmHolder.pm as PM
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onCreate(savedInstanceState: Bundle?) {
        if (presentationModel.currentLifecycleState == null) {
            presentationModel.lifecycleConsumer.accept(PresentationModel.Lifecycle.CREATED)
        }
        navigationMessagesDisposable = presentationModel.navigationMessages.observable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                navigationMessagesDispatcher.dispatch(it)
            }
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
        navigationMessagesDisposable.dispose()
    }
}