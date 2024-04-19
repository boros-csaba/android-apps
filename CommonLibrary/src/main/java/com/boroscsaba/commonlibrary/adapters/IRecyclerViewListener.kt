package com.boroscsaba.commonlibrary.adapters

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Boros Csaba
 */

interface IRecyclerViewListener {
    fun onClick(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder)
    fun onStartDrag(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder)
}
