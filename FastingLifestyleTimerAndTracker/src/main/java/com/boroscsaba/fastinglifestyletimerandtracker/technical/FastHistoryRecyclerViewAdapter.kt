package com.boroscsaba.fastinglifestyletimerandtracker.technical

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import com.boroscsaba.commonlibrary.adapters.DiffUtilCallback
import com.boroscsaba.commonlibrary.ViewHolder
import com.boroscsaba.commonlibrary.settings.SettingsHelper
import com.boroscsaba.fastinglifestyletimerandtracker.R
import com.boroscsaba.fastinglifestyletimerandtracker.model.Fast
import com.boroscsaba.fastinglifestyletimerandtracker.viewmodel.TimerViewModel
import kotlinx.android.synthetic.main.fast_history_layout.view.*
import java.util.*


/**
 * Created by Boros Csaba
 */
class FastHistoryRecyclerViewAdapter(private val context: AppCompatActivity, private var values: ArrayList<Fast>, private val viewModel: TimerViewModel) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    fun changeValues(values: ArrayList<Fast>) {
        val result = DiffUtil.calculateDiff(DiffUtilCallback(this.values, values))
        this.values = values
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.fast_history_layout
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        val fast = values[position]
        val viewHolder = holder as ViewHolder

        val startDateText = SettingsHelper(context.application).getDateFormat().format(fast.startDate)
        viewHolder.itemView.fastDate.text = startDateText

        var milliseconds = fast.endDate - fast.startDate
        val hours = milliseconds / (1000 * 60 * 60)
        milliseconds -= hours * 1000 * 60 * 60
        val minutes = milliseconds / (1000 * 60)
        val durationText = "${hours}h ${String.format("%02d",minutes)}m"
        viewHolder.itemView.duration.text = durationText

        viewHolder.itemView.setOnClickListener {
            viewModel.showFastEditorPopup(fast, context, true)
        }
    }

    override fun getItemCount(): Int {
        return values.size
    }
}