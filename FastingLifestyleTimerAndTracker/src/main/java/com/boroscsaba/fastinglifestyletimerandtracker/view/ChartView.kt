package com.boroscsaba.fastinglifestyletimerandtracker.view

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boroscsaba.commonlibrary.activities.FragmentBase
import com.boroscsaba.commonlibrary.viewelements.charts.ChartData
import com.boroscsaba.fastinglifestyletimerandtracker.R
import com.boroscsaba.fastinglifestyletimerandtracker.viewmodel.TimerViewModel
import kotlinx.android.synthetic.main.fragment_chart.*
import kotlin.math.floor

class ChartView: FragmentBase(R.layout.fragment_chart) {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModel = activity?.run {
            ViewModelProviders.of(this).get(TimerViewModel::class.java)
        }
        viewModel?.fasts?.observe(this, Observer{ value ->
            if (value != null) {
                val chartData = ArrayList(value.map { v -> ChartData(v.startDate, (v.endDate - v.startDate).toDouble() / 1000) })
                barChart.setDataSource(chartData) { v ->
                    var milliseconds = v
                    val hours = floor(milliseconds / (60 * 60)).toInt()
                    milliseconds -= hours * 60 * 60
                    val minutes = floor(milliseconds / 60).toInt()
                    "${hours}h ${String.format("%02d",minutes)}m"
                }
            }
        })
    }
}