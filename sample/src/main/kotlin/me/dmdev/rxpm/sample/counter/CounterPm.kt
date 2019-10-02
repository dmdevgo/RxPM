package me.dmdev.rxpm.sample.counter

import me.dmdev.rxpm.*

class CounterPm : PresentationModel() {

    companion object {
        const val MAX_COUNT = 10
    }

    val count = state(initialValue = 0)

    val minusButtonEnabled = stateOf(
        count.observable.map { it > 0 }
    )

    val plusButtonEnabled = stateOf(
        count.observable.map { it < MAX_COUNT }
    )

    val minusButtonClicks = action<Unit> {
        this.filter { count.value > 0 }
            .map { count.value - 1 }
            .doOnNext(count.consumer)
    }

    val plusButtonClicks = action<Unit> {
        this.filter { count.value < MAX_COUNT }
            .map { count.value + 1 }
            .doOnNext(count.consumer)
    }
}