package me.dmdev.rxpm.base

import android.os.*
import com.bluelinelabs.conductor.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.delegate.*

/**
 * Predefined [Conductor's Controller][RestoreViewOnCreateController] implementing the [PmView][PmView].
 *
 * Just override the [providePresentationModel] and [onBindPresentationModel] methods and you are good to go.
 *
 * If extending is not possible you can implement [PmView],
 * create a [PmControllerDelegate] and pass the lifecycle callbacks to it.
 * See this class's source code for the example.
 */
abstract class PmController<PM : PresentationModel>(args: Bundle? = null) :
    RestoreViewOnCreateController(args),
    PmView<PM> {

    private val delegate by lazy(LazyThreadSafetyMode.NONE) { PmControllerDelegate(this) }

    final override val presentationModel get() = delegate.presentationModel
}