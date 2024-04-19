package com.boroscsaba.fastinglifestyletimerandtracker.view

import android.graphics.Color
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.commonlibrary.activities.FragmentBase
import com.boroscsaba.commonlibrary.viewelements.CalendarEntry
import com.boroscsaba.fastinglifestyletimerandtracker.R
import com.boroscsaba.fastinglifestyletimerandtracker.model.Fast
import com.boroscsaba.fastinglifestyletimerandtracker.technical.FastHistoryRecyclerViewAdapter
import com.boroscsaba.fastinglifestyletimerandtracker.viewmodel.TimerViewModel
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryView: FragmentBase(R.layout.fragment_history) {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModel = activity?.run {
            ViewModelProviders.of(this).get(TimerViewModel::class.java)
        }
        viewModel?.fasts?.observe(this, Observer{ value ->
            if (value != null) {
                if (recyclerView.adapter == null) {
                    recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
                    val adapter = FastHistoryRecyclerViewAdapter(activity as AppCompatActivity, value, viewModel)
                    recyclerView.adapter = adapter
                }
                else {
                    (recyclerView.adapter as FastHistoryRecyclerViewAdapter).changeValues(value)
                    (recyclerView.adapter as FastHistoryRecyclerViewAdapter).notifyDataSetChanged()
                }
                calendar.setEmptyDateOnClickListener { date ->
                    val emptyFast = Fast(context!!)
                    emptyFast.startDate = Utils.getDateAt(date, 9, 0)
                    emptyFast.endDate = Utils.getDateAt(date, 9, 0) + (viewModel.lastTargetHours.value ?: 0) * 1000 * 60 * 60 + (viewModel.lastTargetMinutes.value ?: 0) * 1000 * 60
                    emptyFast.targetHours = viewModel.lastTargetHours.value ?: 4
                    emptyFast.targetMinutes = viewModel.lastTargetMinutes.value ?: 0
                    viewModel.showFastEditorPopup(emptyFast, context!!, false)
                }
                calendar.setCalendarEntries(ArrayList(value.map { fast ->
                    val entry = CalendarEntry(fast.startDate)
                    entry.action = { viewModel.showFastEditorPopup(fast, context!!, true) }
                    entry
                }))
            }
        })
    }
}