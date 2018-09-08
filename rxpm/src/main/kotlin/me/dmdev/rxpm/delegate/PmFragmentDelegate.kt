package me.dmdev.rxpm.delegate

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.base.PmFragment
import me.dmdev.rxpm.navigation.FragmentNavigationMessageDispatcher

/**
 * Delegate for the [Fragment] that helps with creation and binding of
 * a [presentation model][PresentationModel] and a [view][PmView].
 *
 * Use this class only if you can't subclass the [PmFragment].
 *
 * Users of this class must forward all the life cycle methods from the containing Fragment
 * to the corresponding ones in this class.
 */
class PmFragmentDelegate<PM, F>(private val pmView: F)
        where PM : PresentationModel,
              F : Fragment, F : PmView<PM> {


    private val pmHolder: PmHolder by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(pmView).get(PmHolder::class.java)
    }

    internal val pmBinder: PmBinder<PM> by lazy(LazyThreadSafetyMode.NONE) {
        PmBinder(presentationModel, pmView)
    }

    private lateinit var navigationMessagesDisposable: Disposable
    private val navigationMessageDispatcher = FragmentNavigationMessageDispatcher(pmView)

    val presentationModel: PM by lazy(LazyThreadSafetyMode.NONE) {
        if (pmHolder.pm == null) {
            pmHolder.pm = pmView.providePresentationModel()
        }

        @Suppress("UNCHECKED_CAST")
        pmHolder.pm as PM
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onCreate(savedInstanceState: Bundle?) {
        if (presentationModel.currentLifecycleState == null) {
            presentationModel.lifecycleConsumer.accept(PresentationModel.Lifecycle.CREATED)
        }
        navigationMessagesDisposable = presentationModel.navigationMessages.observable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                navigationMessageDispatcher.dispatch(it)
            }
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onStart() {
        pmBinder.bind()
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onResume() {
        pmBinder.bind()
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onSaveInstanceState(outState: Bundle) {
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
    }
}
