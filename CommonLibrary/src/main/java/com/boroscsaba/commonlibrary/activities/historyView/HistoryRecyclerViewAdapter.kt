package com.boroscsaba.commonlibrary.activities.historyView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.boroscsaba.commonlibrary.IDao
import com.boroscsaba.commonlibrary.ViewHolder
import com.boroscsaba.commonlibrary.adapters.DiffUtilCallback
import com.boroscsaba.commonlibrary.viewelements.popupEditor.PopupEditor
import com.boroscsaba.dataaccess.EntityBase

class HistoryRecyclerViewAdapter<T: EntityBase>(private val components: List<Component<T>>, var values: ArrayList<T>, private val classType: Class<T>, private val dao: IDao<T>, private val activity: AppCompatActivity): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun changeValues(values: ArrayList<T>) {
        val result = DiffUtil.calculateDiff(DiffUtilCallback(this.values, values))
        this.values = values
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return getComponentByPosition(position).elementLayoutResourceId
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val component = getComponentByPosition(position)
        val entity = getEntityByPosition(position)
        if (component is ListComponent && entity != null) {
            holder.itemView.setOnClickListener {
                PopupEditor(classType, dao, activity)
                        .withExistingId(entity.id)
                        .show()
            }
        }
        component.onBindViewHolder(holder, entity, classType, dao, activity, values)
    }

    override fun getItemCount(): Int {
        var count = 0
        components.forEach { component ->
            if (component is ListComponent) count += values.count()
            else count++
        }
        return count
    }

    private fun getComponentByPosition(position: Int): Component<T> {
        var counter = 0
        for (component in components) {
            if (component is ListComponent<T>) counter += values.count()
            else counter++

            if (position < counter) {
                return component
            }
        }
        throw IllegalArgumentException("No component found!")
    }

    private fun getEntityByPosition(position: Int): T? {
        for ((offset, component) in components.withIndex()) {
            if (position < offset) return null
            if (component is ListComponent<T>) {
                val index = position - offset
                if (index < values.count())
                    return values[index]
            }
        }
        return null
    }
}