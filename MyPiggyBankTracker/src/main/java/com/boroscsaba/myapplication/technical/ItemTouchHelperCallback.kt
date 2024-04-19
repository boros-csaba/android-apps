package com.boroscsaba.myapplication.technical

import androidx.recyclerview.widget.ItemTouchHelper
import com.boroscsaba.myapplication.view.GoalsListView

/**
 * Created by Boros Csaba
 */

class ItemTouchHelperCallback(private val recyclerView: androidx.recyclerview.widget.RecyclerView, private val adapter: GoalRecyclerViewAdapter) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun getMovementFlags(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, target: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
        adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {}

    override fun onSelectedChanged(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        if (!adapter.isEditMode && adapter.recyclerViewListener is GoalsListView) {
            adapter.isEditMode = true
            adapter.recyclerViewListener.onEditModeChange(true)
            recyclerView.post { adapter.notifyDataSetChanged() }
        }
    }
}
