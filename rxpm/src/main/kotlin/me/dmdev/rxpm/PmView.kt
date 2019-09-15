package me.dmdev.rxpm

import me.dmdev.rxpm.permission.PermissionCaller

/**
 * Interface that need to be implemented by the View part of the RxPM pattern.
 * Has a few useful callbacks and extensions.
 */
interface PmView<PM : PresentationModel> : PermissionCaller {

    /**
     * [PresentationModel] for this view.
     */
    val presentationModel: PM

    /**
     * Provide presentation model to use with this fragment.
     */
    fun providePresentationModel(): PM

    /**
     * Bind to the [Presentation Model][presentationModel] in that method.
     */
    fun onBindPresentationModel(pm: PM)

    /**
     * Called when the view unbinds from the [Presentation Model][presentationModel].
     */
    fun onUnbindPresentationModel() {
        // Nо-op. Override if you need it.
    }
}