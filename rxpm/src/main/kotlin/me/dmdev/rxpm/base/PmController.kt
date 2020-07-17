package me.dmdev.rxpm.base

import android.os.Bundle
import com.bluelinelabs.conductor.Controller
import me.dmdev.rxpm.PmView
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.delegate.PmControllerDelegate

/**
 * Predefined [Conductor's Controller][Controller] implementing the [PmView][PmView].
 *
 * Just override the [providePresentationModel] and [onBindPresentationModel] methods and you are good to go.
 *
 * If extending is not possible you can implement [PmView],
 * create a [PmControllerDelegate] and pass the lifecycle callbacks to it.
 * See this class's source code for the example.
 */
abstract class PmController<PM : PresentationModel>(args: Bundle? = null) :
    Controller(args),
    PmView<PM> {

    @Suppress("LeakingThis")
    private val delegate = PmControllerDelegate(this)

    final override val presentationModel get() = delegate.presentationModel
}