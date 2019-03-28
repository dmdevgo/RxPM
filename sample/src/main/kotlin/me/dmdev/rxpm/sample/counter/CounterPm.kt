package me.dmdev.rxpm.sample.counter

import me.dmdev.rxpm.PresentationModel

class CounterPm : PresentationModel() {

    companion object {
        const val MAX_COUNT = 10
    }

    val count = State(initialValue = 0)
    val minusButtonEnabled = State(false)
    val plusButtonEnabled = State(false)

    val minusButtonClicks = Action<Unit>()
    val plusButtonClicks = Action<Unit>()

    override fun onCreate() {
        super.onCreate()

        count.observable
            .map { it > 0 }
            .subscribe(minusButtonEnabled.consumer)
            .untilDestroy()

        count.observable
            .map { it < MAX_COUNT }
            .subscribe(plusButtonEnabled.consumer)
            .untilDestroy()

        minusButtonClicks.observable
            .filter { count.value > 0 }
            .map { count.value - 1 }
            .subscribe(count.consumer)
            .untilDestroy()

        plusButtonClicks.observable
            .filter { count.value < MAX_COUNT }
            .map { count.value + 1 }
            .subscribe(count.consumer)
            .untilDestroy()
    }
}