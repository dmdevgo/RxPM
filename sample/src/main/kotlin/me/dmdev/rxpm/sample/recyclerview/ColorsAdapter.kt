package me.dmdev.rxpm.sample.recyclerview

import android.graphics.*
import android.view.*
import com.jakewharton.rxbinding3.view.*
import kotlinx.android.synthetic.main.header_listcontrol.view.*
import kotlinx.android.synthetic.main.item_listcontrol.view.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.main.extensions.*
import me.dmdev.rxpm.sample.recyclerview.RecyclerViewPm.*
import me.dmdev.rxpm.widget.*

class ColorsAdapter(listControl: ListControl<BaseItem>) : ListControlAdapter<BaseItem>(listControl) {

    override fun createViewHolderForPresentationModel(
        parent: ViewGroup,
        pm: PresentationModel
    ): ItemViewHolder<ItemPresentationModel<BaseItem>> {

        return when (pm) {
            is HeaderPm -> HeaderPmViewHolder(pm, parent.inflate(R.layout.header_listcontrol))
            is RedItemPm -> RedItemViewHolder(pm, parent.inflate(R.layout.item_listcontrol))
            is YellowItemPm -> YellowItemViewHolder(pm, parent.inflate(R.layout.item_listcontrol))
            is GreenItemPm -> GreenItemViewHolder(pm, parent.inflate(R.layout.item_listcontrol))
            else -> throw IllegalArgumentException("")
        } as ItemViewHolder<ItemPresentationModel<BaseItem>>

    }

    inner class HeaderPmViewHolder(pm: HeaderPm, itemView: View) : ItemViewHolder<HeaderPm>(pm, itemView) {

        init {
            itemView.redCheckedCount.setBackgroundColor(Color.RED)
            itemView.yellowCheckedCount.setBackgroundColor(Color.YELLOW)
            itemView.greenCheckedCount.setBackgroundColor(Color.GREEN)
        }

        override fun onBindPresentationModel() {

            itemPm.checkedRedCount bindTo {
                itemView.redCheckedCount.text = it.toString()
            }

            itemPm.checkedYellowCount bindTo {
                itemView.yellowCheckedCount.text = it.toString()
            }

            itemPm.checkedGreenCount bindTo {
                itemView.greenCheckedCount.text = it.toString()
            }
        }
    }

    inner class RedItemViewHolder(
        pm: RedItemPm,
        itemView: View
    ) : ItemViewHolder<RedItemPm>(pm, itemView) {

        init {
            itemView.coloredView.setBackgroundColor(Color.RED)
        }

        override fun onBindPresentationModel() {
            super.onBindPresentationModel()

            itemPm.item bindTo {
                itemView.title.text = it.id.toString()
            }

            itemPm.checkControl bindTo itemView.checkbox

            itemView.clicks() bindTo itemPm.itemClicks
        }
    }

    inner class YellowItemViewHolder(
        private val yellowItemPm: YellowItemPm,
        itemView: View
    ) : ItemViewHolder<YellowItemPm>(yellowItemPm, itemView) {

        init {
            itemView.coloredView.setBackgroundColor(Color.YELLOW)
        }

        override fun onBindPresentationModel() {
            super.onBindPresentationModel()

            yellowItemPm.item bindTo {
                itemView.title.text = it.id.toString()
            }

            yellowItemPm.checkControl bindTo itemView.checkbox

            itemView.clicks() bindTo itemPm.itemClicks

        }
    }

    inner class GreenItemViewHolder(
        pm: GreenItemPm,
        itemView: View
    ) : ItemViewHolder<GreenItemPm>(pm, itemView) {

        init {
            itemView.coloredView.setBackgroundColor(Color.GREEN)
        }

        override fun onBindPresentationModel() {
            super.onBindPresentationModel()

            itemPm.item bindTo {
                itemView.title.text = it.id.toString()
            }

            itemPm.checkControl bindTo itemView.checkbox

            itemView.clicks() bindTo itemPm.itemClicks
        }
    }
}