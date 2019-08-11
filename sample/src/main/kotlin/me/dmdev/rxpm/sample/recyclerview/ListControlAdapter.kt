package me.dmdev.rxpm.sample.recyclerview

import android.view.*
import androidx.recyclerview.widget.*
import me.dmdev.rxpm.*

abstract class ListControlAdapter<T : Any>(
    private val listControl: ListControl<T>,
    diffCallback: DiffUtil.ItemCallback<T> = SimpleItemDiffCallback()
) : ListAdapter<T, ListControlAdapter.ItemViewHolder<T>>(diffCallback) {

    final override fun onCreateViewHolder(parent: ViewGroup, type: Int): ItemViewHolder<T> {
        return createViewHolderForPresentationModel(parent, listControl.getItemPresentationModelForType(type)).apply {
            this.onBindPresentationModel()
        }
    }

    abstract fun createViewHolderForPresentationModel(
        parent: ViewGroup,
        pm: PresentationModel
    ): ItemViewHolder<T>

    override fun onViewAttachedToWindow(holder: ItemViewHolder<T>) {
        super.onViewAttachedToWindow(holder)
    }

    override fun getItemViewType(position: Int): Int {
        return listControl.getType(listControl.items.value[position])
    }

    override fun onBindViewHolder(holder: ItemViewHolder<T>, position: Int) {
        holder.itemPm.changeItem.consumer.accept(listControl.items.value[position])
    }

    open class ItemViewHolder<T: Any>(itemView: View, val itemPm: ItemPresentationModel<T>) :
        RecyclerView.ViewHolder(itemView) {
        open fun onBindPresentationModel() {}
    }
}