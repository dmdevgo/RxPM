package me.dmdev.rxpm

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Interface that need to be implemented by the View part of the RxPM pattern.
 * Has a few useful callbacks and extensions.
 * @author Dmitriy Gorbunov
 */
interface PmView<PM : PresentationModel> {

    /**
     * [PresentationModel] for this view.
     */
    val presentationModel: PM

    /**
     * [CompositeDisposable] that will be CLEARED ON [UNBIND][PresentationModel.Lifecycle.UNBINDED].
     */
    val compositeUnbind: CompositeDisposable

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

    /**
     * Local extension to add this [Disposable] to the [CompositeDisposable][compositeUnbind]
     * that will be CLEARED ON [UNBIND][PresentationModel.Lifecycle.UNBINDED].
     */
    fun Disposable.untilUnbind() = compositeUnbind.add(this)
}