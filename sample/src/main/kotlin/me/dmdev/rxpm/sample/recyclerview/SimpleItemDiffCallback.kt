package me.dmdev.rxpm.sample.recyclerview

import android.annotation.*
import androidx.recyclerview.widget.*

class SimpleItemDiffCallback<T: Any> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return if (oldItem is Identifiable<*> && newItem is Identifiable<*>) {
            oldItem.id == newItem.id
        } else {
            oldItem === newItem
        }
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

}