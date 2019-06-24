package me.dmdev.rxpm.delegate

import android.os.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.PresentationModel.*
import me.dmdev.rxpm.navigation.*
import java.util.*

/**
 *  Common delegate serves for forwarding the lifecycle[PresentationModel.Lifecycle] directly into the [PresentationModel][PresentationModel].
 *  Can be used to implement your own delegate for the View[PmView].
 *
 *  @see PmActivityDelegate
 *  @see PmFragmentDelegate
 *  @see PmControllerDelegate
 */
class CommonDelegate<PM, V>(
    private val pmView: PmView<PM>,
    private val navigationMessagesDispatcher: NavigationMessageDispatcher
)
        where PM : PresentationModel,
              V : PmView<PM> {

    companion object {
        private const val SAVED_PM_TAG_KEY = "_rxpm_presentation_model_tag"
    }

    private lateinit var pmTag: String

    val presentationModel: PM by lazy(LazyThreadSafetyMode.NONE) {
        @Suppress("UNCHECKED_CAST")
        PmStore.getPm(pmTag) { pmView.providePresentationModel() } as PM
    }

    fun onCreate(savedInstanceState: Bundle?) {
        pmTag = savedInstanceState?.getString(SAVED_PM_TAG_KEY) ?: UUID.randomUUID().toString()
        if (presentationModel.currentLifecycleState == null) {
            presentationModel.lifecycleConsumer.accept(Lifecycle.CREATED)
        }
    }

    fun onBind() {

        val pm = presentationModel

        if (pm.currentLifecycleState == Lifecycle.CREATED
            || pm.currentLifecycleState == Lifecycle.UNBINDED
        ) {
            pm.lifecycleConsumer.accept(Lifecycle.BINDED)
            pmView.onBindPresentationModel(pm)

            if (pm is NavigationalPm) {
                pm.navigationMessages bindTo {
                    navigationMessagesDispatcher.dispatch(it)
                }
            }
        }
    }

    fun onResume() {
        if (presentationModel.currentLifecycleState == Lifecycle.BINDED
            || presentationModel.currentLifecycleState == Lifecycle.PAUSED
        ) {
            presentationModel.lifecycleConsumer.accept(Lifecycle.RESUMED)
        }
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SAVED_PM_TAG_KEY, pmTag)
    }

    fun onPause() {
        if (presentationModel.currentLifecycleState == Lifecycle.RESUMED) {
            presentationModel.lifecycleConsumer.accept(Lifecycle.PAUSED)
        }
    }

    fun onUnbind() {
        if (presentationModel.currentLifecycleState == Lifecycle.PAUSED
            || presentationModel.currentLifecycleState == Lifecycle.BINDED
        ) {
            pmView.onUnbindPresentationModel()
            presentationModel.lifecycleConsumer.accept(Lifecycle.UNBINDED)
        }
    }

    fun onDestroy() {
        if (presentationModel.currentLifecycleState == Lifecycle.CREATED
            || presentationModel.currentLifecycleState == Lifecycle.UNBINDED
        ) {
            PmStore.removePm(pmTag)
            presentationModel.lifecycleConsumer.accept(Lifecycle.DESTROYED)
        }
    }
}