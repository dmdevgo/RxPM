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
    val pm: PM
    val compositeDisposable: CompositeDisposable
    fun providePresentationModel(): PM
    fun onBindPresentationModel()
    fun onUnbindPresentationModel()
    fun <T> Observable<T>.bindTo(consumer: Consumer<in T>) {
        compositeDisposable.add(
                this
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(consumer)
        )
    }

    fun <T> Observable<T>.bindTo(consumer: (T) -> Unit) {
        compositeDisposable.add(
                this
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(consumer)
        )
    }
    fun Disposable.untilUnbind() = compositeDisposable.add(this)
}