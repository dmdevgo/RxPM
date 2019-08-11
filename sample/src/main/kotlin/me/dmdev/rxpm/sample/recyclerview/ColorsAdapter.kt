package me.dmdev.rxpm.sample.recyclerview

import android.graphics.*
import android.view.*
import kotlinx.android.synthetic.main.item_listcontrol.view.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.main.extensions.*

class ColorsAdapter(listControl: ListControl<RecyclerViewPm.ColorItem>) :
    ListControlAdapter<RecyclerViewPm.ColorItem>(listControl) {

    override fun createViewHolderForPresentationModel(
        parent: ViewGroup,
        pm: PresentationModel
    ): ItemViewHolder<RecyclerViewPm.ColorItem> {

        return when (pm) {
            is RecyclerViewPm.RedItemPm -> RedItemViewHolder(parent.inflate(R.layout.item_listcontrol), pm)
            is RecyclerViewPm.YellowItemPm -> YellowItemViewHolder(parent.inflate(R.layout.item_listcontrol), pm)
            is RecyclerViewPm.GreenItemPm -> GreenItemViewHolder(parent.inflate(R.layout.item_listcontrol), pm)
            else -> throw IllegalArgumentException("")
        } as ItemViewHolder<RecyclerViewPm.ColorItem>

    }

    class RedItemViewHolder(itemView: View, redItemPm: RecyclerViewPm.RedItemPm) :
        ItemViewHolder<RecyclerViewPm.RedItem>(itemView, redItemPm) {

        init {
            itemView.setBackgroundColor(Color.RED)
            itemView.title.setTextColor(Color.WHITE)
        }

        override fun onBindPresentationModel() {
            super.onBindPresentationModel()

            itemPm.item bindTo {
                itemView.title.text = it.id.toString()
            }
        }
    }

    class YellowItemViewHolder(itemView: View, yellowItemPm: RecyclerViewPm.YellowItemPm) :
        ItemViewHolder<RecyclerViewPm.YellowItem>(itemView, yellowItemPm) {

        init {
            itemView.setBackgroundColor(Color.YELLOW)
            itemView.title.setTextColor(Color.BLACK)
        }

        override fun onBindPresentationModel() {
            super.onBindPresentationModel()

            itemPm.item bindTo {
                itemView.title.text = it.id.toString()
            }
        }
    }

    class GreenItemViewHolder(itemView: View, greenItemPm: RecyclerViewPm.GreenItemPm) :
        ItemViewHolder<RecyclerViewPm.GreenItem>(itemView, greenItemPm) {

        init {
            itemView.setBackgroundColor(Color.GREEN)
            itemView.title.setTextColor(Color.BLACK)
        }

        override fun onBindPresentationModel() {
            super.onBindPresentationModel()

            itemPm.item bindTo {
                itemView.title.text = it.id.toString()
            }
        }
    }
}