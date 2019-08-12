package me.dmdev.rxpm.sample.recyclerview

import io.reactivex.rxkotlin.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.widget.*
import kotlin.random.*

class RecyclerViewPm : PresentationModel() {

    private val checkedRed = RxMutableSet<Int>()
    private val checkedYellow = RxMutableSet<Int>()
    private val checkedGreen = RxMutableSet<Int>()

    val itemsControl = listControl<BaseItem> {
        registerItemPmProvider(ItemPmProvider(Header::class.java) { HeaderPm() })
        registerItemPmProvider(ItemPmProvider(RedItem::class.java) { RedItemPm() })
        registerItemPmProvider(ItemPmProvider(YellowItem::class.java) { YellowItemPm() })
        registerItemPmProvider(ItemPmProvider(GreenItem::class.java) { GreenItemPm() })
    }

    interface BaseItem
    interface ColorItem : BaseItem, Identifiable<Int> {
        override val id: Int
    }

    class Header : BaseItem
    data class RedItem(override val id: Int) : ColorItem
    data class YellowItem(override val id: Int) : ColorItem
    data class GreenItem(override val id: Int) : ColorItem

    inner class HeaderPm : ItemPresentationModel<Header>() {

        val checkedRedCount = state(0)
        val checkedYellowCount = state(0)
        val checkedGreenCount = state(0)

        override fun onCreate() {
            super.onCreate()

            checkedRed.observable
                .map { it.size }
                .subscribe(checkedRedCount.consumer)
                .untilDestroy()

            checkedYellow.observable
                .map { it.size }
                .subscribe(checkedYellowCount.consumer)
                .untilDestroy()

            checkedGreen.observable
                .map { it.size }
                .subscribe(checkedGreenCount.consumer)
                .untilDestroy()
        }

    }

    abstract class ColorItemPm<T : ColorItem>(
        private val checkedSet: RxMutableSet<Int>
    ) : ItemPresentationModel<ColorItem>() {

        val checkControl = checkControl()

        val itemClicks = action<Unit>()

        override fun onCreate() {
            super.onCreate()

            item.observable
                .withLatestFrom(checkedSet.observable) { item, checkedSet ->
                    checkedSet.contains(item.id)
                }
                .subscribe(checkControl.checked.consumer)
                .untilDestroy()

            checkControl.checkedChanges.observable
                .withLatestFrom(item.observable) { isChecked, item ->
                    if (isChecked) {
                        checkedSet.add(item.id)
                    } else {
                        checkedSet.remove(item.id)
                    }
                }
                .subscribe()
                .untilDestroy()

            itemClicks.observable
                .withLatestFrom(item.observable) { _, item ->
                    checkedSet.contains(item.id).not()
                }
                .subscribe(checkControl.checkedChanges.consumer)
                .untilDestroy()
        }
    }

    inner class RedItemPm : ColorItemPm<RedItem>(checkedRed)
    inner class YellowItemPm : ColorItemPm<YellowItem>(checkedYellow)
    inner class GreenItemPm : ColorItemPm<GreenItem>(checkedGreen)

    override fun onCreate() {
        super.onCreate()

        val random = Random(System.currentTimeMillis())

        val list = listOf(Header()).plus(
            List(100) { index ->
                when (random.nextInt(0, 3)) {
                    0 -> RedItem(index)
                    1 -> YellowItem(index)
                    else -> GreenItem(index)
                }
            }
        )

        itemsControl.items.consumer.accept(list)
    }
}

