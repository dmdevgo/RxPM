package me.dmdev.rxpm

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/**
 * @author Dmitriy Gorbunov
 */
interface PmView<out PM : PresentationModel> {

    /**
     * [PresentationModel] for this view.
     */
    val presentationModel: PM

    /**
     * Subscriptions list that will be cleared on unbind
     */
    val compositeUnbind: CompositeDisposable

    /**
     * Provide presentation model to use with this fragment.
     */
    fun providePresentationModel(): PM

    /**
     * Bind to the [Presentation Model][presentationModel] in that method.
     * Use convenient extensions [bindTo] (all subscriptions done using it will be cleared on [unbind][compositeUnbind]).
     */
    fun onBindPresentationModel(pm: PresentationModel)


    /**
     * Called when the view unbinds from the [Presentation Model][presentationModel].
     */
    fun onUnbindPresentationModel()

    /**
     * Local extension to subscribe to the observable and add it to the subscriptions list
     * that will be CLEARED [ON UNBIND][compositeUnbind], so use it ONLY in [onBindPresentationModel].
     */
    fun <T> Observable<T>.bindTo(consumer: Consumer<in T>) {
        compositeUnbind.add(
                this
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(consumer)
        )
    }

    /**
     * Local extension to subscribe to the observable and add it to the subscriptions list
     * that will be CLEARED [ON UNBIND][compositeUnbind], so use it ONLY in [onBindPresentationModel].
     */
    fun <T> Observable<T>.bindTo(consumer: (T) -> Unit) {
        compositeUnbind.add(
                this
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(consumer)
        )
    }

    /**
     * Local extension to pass the empty value to the [Consumer].
     */
    fun passTo(consumer: Consumer<Unit>) {
        consumer.accept(Unit)
    }

    /**
     * Local extension to pass the value to the [Consumer].
     */
    fun <T> T.passTo(consumer: Consumer<T>) {
        consumer.accept(this)
    }

    /**
     * Add this chain to the subscriptions list that will be cleared [on unbind][compositeUnbind]
     */
    fun Disposable.untilUnbind() = compositeUnbind.add(this)
}