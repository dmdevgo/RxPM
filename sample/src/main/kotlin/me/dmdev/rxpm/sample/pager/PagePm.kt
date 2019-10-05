package me.dmdev.rxpm.sample.pager

import me.dmdev.rxpm.*
import timber.log.*

class PagePm(
    private val pageNumber: Int
) : PresentationModel() {

    init {
        lifecycleObservable.doOnNext {
            Timber.d("PM #$pageNumber $it")
        }.subscribe().untilDestroy()
    }
}