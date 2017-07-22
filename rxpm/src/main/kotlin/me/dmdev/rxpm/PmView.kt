package me.dmdev.rxpm

import io.reactivex.disposables.CompositeDisposable

/**
 * @author Dmitriy Gorbunov
 */
interface PmView<out PM : PresentationModel> {
    val pm: PM
    val compositeDisposable: CompositeDisposable
    fun providePresentationModel(): PM
    fun onBindPresentationModel()
    fun onUnbindPresentationModel()
}