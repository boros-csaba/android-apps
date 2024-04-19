package com.boroscsaba.commonlibrary.adapters

import androidx.recyclerview.widget.DiffUtil
import com.boroscsaba.dataaccess.IObjectWithId

/**
* Created by boros on 2/11/2018.
*/
class DiffUtilCallback<T: IObjectWithId>(private val oldList: ArrayList<T>, private val newList: ArrayList<T>): DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return true
    }
}