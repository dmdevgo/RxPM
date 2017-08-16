package me.dmdev.rxpm

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * @author Dmitriy Gorbunov
 */
interface PmView<PM : PresentationModel> {

    /**
     * [PresentationModel] for this view.
     */
    val presentationModel: PM

    /**
     * Subscriptions list that will be cleared on unbind.
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
     * Add this chain to the subscriptions list that will be cleared [on unbind][compositeUnbind].
     */
    fun Disposable.untilUnbind() = compositeUnbind.add(this)
}