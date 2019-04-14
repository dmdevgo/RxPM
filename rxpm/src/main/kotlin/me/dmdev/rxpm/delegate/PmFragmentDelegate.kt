package me.dmdev.rxpm.delegate

import android.os.*
import android.support.v4.app.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.base.*
import me.dmdev.rxpm.navigation.*
import java.util.*

/**
 * Delegate for the [Fragment] that helps with creation and binding of
 * a [presentation model][PresentationModel] and a [view][PmView].
 *
 * Use this class only if you can't subclass the [PmFragment].
 *
 * Users of this class must forward all the life cycle methods from the containing Fragment
 * to the corresponding ones in this class.
 * todo doc
 */
class PmFragmentDelegate<PM, F>(
    private val pmFragment: F,
    private val retainMode: RetainMode = RetainMode.SAVED_STATE
)
        where PM : PresentationModel,
              F : Fragment, F : PmView<PM> {

    companion object {
        private const val SAVED_PM_TAG_KEY = "_rxpm_presentation_model_tag"
    }

    private lateinit var pmTag: String

    // todo doc
    enum class RetainMode { SAVED_STATE, CONFIGURATION_CHANGES }

    private val navigationMessageDispatcher = SupportFragmentNavigationMessageDispatcher(pmFragment)

    val presentationModel: PM by lazy(LazyThreadSafetyMode.NONE) {
        @Suppress("UNCHECKED_CAST")
        PmStore.getPm(pmTag) { pmFragment.providePresentationModel() } as PM
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onCreate(savedInstanceState: Bundle?) {
        pmTag = savedInstanceState?.getString(SAVED_PM_TAG_KEY) ?: UUID.randomUUID().toString()
        presentationModel.lifecycleConsumer.accept(PresentationModel.Lifecycle.CREATED)
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onViewCreated() {
        presentationModel.lifecycleConsumer.accept(PresentationModel.Lifecycle.BINDED)
        presentationModel.navigationMessages bindTo {
            navigationMessageDispatcher.dispatch(it)
        }
        pmFragment.onBindPresentationModel(presentationModel)
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
        presentationModel.lifecycleConsumer.accept(PresentationModel.Lifecycle.RESUMED)
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SAVED_PM_TAG_KEY, pmTag)
        presentationModel.lifecycleConsumer.accept(PresentationModel.Lifecycle.PAUSED)
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onPause() {
        presentationModel.lifecycleConsumer.accept(PresentationModel.Lifecycle.PAUSED)
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
        pmFragment.onUnbindPresentationModel()
        presentationModel.lifecycleConsumer.accept(PresentationModel.Lifecycle.UNBINDED)
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onDestroy() {
        when (retainMode) {
            RetainMode.SAVED_STATE -> {
                if (pmFragment.activity?.isFinishing == true
                    || (pmFragment.fragmentManager?.isStateSaved?.not() == true)
                ) {
                    presentationModel.lifecycleConsumer.accept(PresentationModel.Lifecycle.DESTROYED)
                }
            }

            RetainMode.CONFIGURATION_CHANGES -> {
                if (pmFragment.activity?.isChangingConfigurations?.not() == true) {
                    presentationModel.lifecycleConsumer.accept(PresentationModel.Lifecycle.DESTROYED)
                }
            }
        }
    }
}
