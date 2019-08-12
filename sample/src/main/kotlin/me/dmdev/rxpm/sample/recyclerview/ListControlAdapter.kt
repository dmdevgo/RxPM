package me.dmdev.rxpm.sample.recyclerview

import android.view.*
import androidx.recyclerview.widget.*
import me.dmdev.rxpm.*

abstract class ListControlAdapter<T : Any>(
    private val listControl: ListControl<T>,
    diffCallback: DiffUtil.ItemCallback<T> = SimpleItemDiffCallback()
) : ListAdapter<T, ListControlAdapter<T>.ItemViewHolder<ItemPresentationModel<T>>>(diffCallback) {

    final override fun onCreateViewHolder(parent: ViewGroup, type: Int): ItemViewHolder<ItemPresentationModel<T>> {
        return createViewHolderForPresentationModel(parent, listControl.getItemPresentationModelForType(type)).apply {
            this.onBindPresentationModel()
        }
    }

    abstract fun createViewHolderForPresentationModel(
        parent: ViewGroup,
        pm: PresentationModel
    ): ItemViewHolder<ItemPresentationModel<T>>

    override fun getItemViewType(position: Int): Int {
        return listControl.getType(listControl.items.value[position])
    }

    override fun onBindViewHolder(holder: ItemViewHolder<ItemPresentationModel<T>>, position: Int) {
        holder.itemPm.changeItem.consumer.accept(listControl.items.value[position])
    }

    open inner class ItemViewHolder<PM : ItemPresentationModel<T>>(
        val itemPm: PM,
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        open fun onBindPresentationModel() {}
    }
}