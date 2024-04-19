package com.boroscsaba.commonlibrary.adapters

import androidx.recyclerview.widget.DiffUtil

class ListDiffUtilCallback<T>(private val oldList: ArrayList<T>, private val newList: ArrayList<T>, private val itemComparator: (T, T) -> Boolean, private val contentComparator: (T, T) -> Boolean): DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return itemComparator(oldList[oldItemPosition], newList[newItemPosition])
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return contentComparator(oldList[oldItemPosition], newList[newItemPosition])
    }
}