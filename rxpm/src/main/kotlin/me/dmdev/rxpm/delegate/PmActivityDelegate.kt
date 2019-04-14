package me.dmdev.rxpm.delegate

import android.app.*
import android.os.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.PresentationModel.*
import me.dmdev.rxpm.base.*
import me.dmdev.rxpm.navigation.*
import java.util.*

/**
 * Delegate for the [Activity] that helps with creation and binding of
 * a [presentation model][PresentationModel] and a [view][PmView].
 *
 * Use this class only if you can't subclass the [PmActivity].
 *
 * Users of this class must forward all the life cycle methods from the containing Activity
 * to the corresponding ones in this class.
 * todo doc
 */
class PmActivityDelegate<PM, A>(
    private val pmActivity: A,
    private val retainMode: RetainMode = RetainMode.FINISHING
)
        where PM : PresentationModel,
              A : Activity, A : PmView<PM> {

    companion object {
        private const val SAVED_PM_TAG_KEY = "_rxpm_presentation_model_tag"
    }

    // todo doc
    enum class RetainMode { FINISHING, CONFIGURATION_CHANGES }

    private lateinit var pmTag: String

    private val navigationMessagesDispatcher = ActivityNavigationMessageDispatcher(pmActivity)

    val presentationModel: PM by lazy(LazyThreadSafetyMode.NONE) {
        @Suppress("UNCHECKED_CAST")
        PmStore.getPm(pmTag) { pmActivity.providePresentationModel() } as PM
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onCreate(savedInstanceState: Bundle?) {
        pmTag = savedInstanceState?.getString(SAVED_PM_TAG_KEY) ?: UUID.randomUUID().toString()

        presentationModel.lifecycleConsumer.accept(Lifecycle.CREATED)
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onPostCreate() {
        presentationModel.lifecycleConsumer.accept(Lifecycle.BINDED)

        presentationModel.navigationMessages bindTo {
            navigationMessagesDispatcher.dispatch(it)
        }

        pmActivity.onBindPresentationModel(presentationModel)
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onStart() {
        // For symmetry, may be used in the future
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onResume() {
        presentationModel.lifecycleConsumer.accept(Lifecycle.RESUMED)
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SAVED_PM_TAG_KEY, pmTag)
        presentationModel.lifecycleConsumer.accept(Lifecycle.PAUSED)
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onPause() {
        presentationModel.lifecycleConsumer.accept(Lifecycle.PAUSED)
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onStop() {
        // For symmetry, may be used in the future
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onDestroy() {
        pmActivity.onUnbindPresentationModel()
        presentationModel.lifecycleConsumer.accept(Lifecycle.UNBINDED)

        when (retainMode) {
            RetainMode.FINISHING -> {
                if (pmActivity.isFinishing) {
                    presentationModel.lifecycleConsumer.accept(Lifecycle.DESTROYED)
                }
            }

            RetainMode.CONFIGURATION_CHANGES -> {
                if (!pmActivity.isChangingConfigurations) {
                    presentationModel.lifecycleConsumer.accept(Lifecycle.DESTROYED)
                }
            }
        }
    }
}