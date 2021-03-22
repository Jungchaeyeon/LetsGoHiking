package com.hdh.base.recycler

import androidx.recyclerview.widget.DiffUtil

class BaseDiffUtil<T>(
    private val oldItems: List<T>,
    private val newItems: List<T>,
    private val contentsTheSame: (old: T, new: T) -> Boolean,
    private val itemsTheSame: ((old: T, new: T) -> Boolean)?,
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldItems.size
    }

    override fun getNewListSize(): Int {
        return newItems.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return itemsTheSame?.invoke(
            oldItems[oldItemPosition],
            newItems[newItemPosition]
        ) ?: oldItemPosition == newItemPosition
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return contentsTheSame.invoke(oldItems[oldItemPosition], newItems[newItemPosition])
    }
}