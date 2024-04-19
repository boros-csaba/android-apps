package com.boroscsaba.bodymeasurementtracker.technical

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import com.boroscsaba.bodymeasurementtracker.R
import com.boroscsaba.commonlibrary.ViewHolder
import kotlinx.android.synthetic.main.measurement_history_item_layout.view.*
import android.widget.ListView
import com.boroscsaba.bodymeasurementtracker.model.MeasurementLogEntry
import com.boroscsaba.commonlibrary.adapters.ListDiffUtilCallback
import com.boroscsaba.commonlibrary.settings.SettingsHelper


/**
 * Created by Boros Csaba
 */
class MeasurementHistoryRecyclerViewAdapter(private val context: AppCompatActivity, private var values: ArrayList<List<MeasurementLogEntry>>) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    fun changeValues(values: ArrayList<List<MeasurementLogEntry>>) {
        val result = DiffUtil.calculateDiff(ListDiffUtilCallback(this.values, values,
                { value1, value2 -> value1.first() == value2.first() },
                { value1, value2 -> value1.first() == value2.first() &&
                        value1.all { v ->
                            val index = value1.indexOf(v)
                            value2.count() > index && value2[index].value == v.value
                        }
                }
        ))

        this.values = values
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.measurement_history_item_layout
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        val measurements = values[position]
        val viewHolder = holder as ViewHolder

        viewHolder.itemView.dateText.text = SettingsHelper(context.application).getDateFormat().format(measurements.first().logDate)
        viewHolder.itemView.listView.adapter = MeasurementHistoryEntryListViewAdapter(context.applicationContext, measurements)
        setDynamicHeight(viewHolder.itemView.listView)
    }

    private fun setDynamicHeight(mListView: ListView) {
        val mListAdapter = mListView.adapter ?: return
        val params = mListView.layoutParams
        val itemHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, context.resources.displayMetrics).toInt()
        params.height = mListAdapter.count * itemHeight + mListView.dividerHeight * (mListAdapter.count - 1)
        mListView.layoutParams = params
        mListView.requestLayout()
    }

    override fun getItemCount(): Int {
        return values.size
    }
}