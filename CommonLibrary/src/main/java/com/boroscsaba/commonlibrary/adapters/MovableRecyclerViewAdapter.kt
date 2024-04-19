package com.boroscsaba.commonlibrary.adapters

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import com.boroscsaba.dataaccess.IObjectWithId
import com.boroscsaba.commonlibrary.ViewHolder

open class MovableRecyclerViewAdapter<T: IObjectWithId>(private val recyclerViewListener: IRecyclerViewListener?, var values: ArrayList<T>, private val layout: Int, private val reorderHandleId: Int, private val onBindViewHolder: (holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) -> Unit) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

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
        return layout
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        val viewModel = values[position]
        val viewHolder = holder as ViewHolder
        viewHolder.id = viewModel.id

        val handle = holder.itemView.findViewById<ImageView>(reorderHandleId)
        handle.setOnTouchListener { _, event ->
            if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_DOWN) {
                recyclerViewListener?.onStartDrag(holder)
            }
            false
        }
        onBindViewHolder.invoke(holder, position)
    }

    internal fun onItemMove(fromPosition: Int, toPosition: Int) {
        val item1 = values[fromPosition]
        val item2 = values[toPosition]
        values[fromPosition] = item2
        values[toPosition] = item1

        notifyItemMoved(fromPosition, toPosition)
    }

    override fun getItemCount(): Int {
        return values.size
    }
}