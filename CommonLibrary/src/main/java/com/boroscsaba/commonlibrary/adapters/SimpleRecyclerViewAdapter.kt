package com.boroscsaba.commonlibrary.adapters

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import com.boroscsaba.dataaccess.IObjectWithId
import com.boroscsaba.commonlibrary.ViewHolder

open class SimpleRecyclerViewAdapter<T: IObjectWithId>(var values: ArrayList<T>, private val layout: Int, private val onBindViewHolder: (holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, element: T) -> Unit, private var footerLayout: Int? = null, private val onBindFooter: ((holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) -> Unit)? = null) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    fun changeValues(values: ArrayList<T>) {
        val result = DiffUtil.calculateDiff(DiffUtilCallback(this.values, values))
        this.values = values
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == values.size) {
            footerLayout ?: layout
        }
        else {
            layout
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (position == values.size) {
            onBindFooter?.invoke(holder, position)
        }
        else {
            val viewModel = values[position]
            val viewHolder = holder as ViewHolder
            viewHolder.id = viewModel.id
            onBindViewHolder.invoke(holder, values[position])
        }
    }

    override fun getItemCount(): Int {
        return values.size + if (footerLayout == null) 0 else 1
    }
}