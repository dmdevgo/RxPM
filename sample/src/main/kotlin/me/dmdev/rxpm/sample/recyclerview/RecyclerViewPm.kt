package me.dmdev.rxpm.sample.recyclerview

import me.dmdev.rxpm.*
import kotlin.random.*

class RecyclerViewPm : PresentationModel() {

    val itemsControl = listControl<ColorItem>().apply {
        registerItemPmProvider(
            itemPmProvider { RedItemPm() }
        )

        registerItemPmProvider(
            itemPmProvider { YellowItemPm() }
        )

        registerItemPmProvider(
            itemPmProvider { GreenItemPm() }
        )
    }

    interface ColorItem { val id: Int }

    class RedItem(override val id: Int) : ColorItem
    class YellowItem(override val id: Int) : ColorItem
    class GreenItem(override val id: Int) : ColorItem

    inner class RedItemPm : ItemPresentationModel<RedItem>()
    inner class YellowItemPm : ItemPresentationModel<YellowItem>()
    inner class GreenItemPm : ItemPresentationModel<GreenItem>()

    override fun onCreate() {
        super.onCreate()

        val random = Random(System.currentTimeMillis())

        val list = List(100) { index ->
            when (random.nextInt(0, 3)) {
                0 -> RedItem(index)
                1 -> YellowItem(index)
                else -> GreenItem(index)
            }
        }

        itemsControl.items.consumer.accept(list )
    }
}

