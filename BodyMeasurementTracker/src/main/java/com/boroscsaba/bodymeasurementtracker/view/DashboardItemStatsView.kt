package com.boroscsaba.bodymeasurementtracker.view

import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boroscsaba.bodymeasurementtracker.R
import com.boroscsaba.bodymeasurementtracker.model.Parameter
import com.boroscsaba.bodymeasurementtracker.viewmodel.ParameterViewModel
import com.boroscsaba.commonlibrary.activities.FragmentBase
import com.boroscsaba.commonlibrary.viewelements.charts.ChartData
import kotlinx.android.synthetic.main.dashboard_item_stats_view.*

class DashboardItemStatsView : FragmentBase(R.layout.dashboard_item_stats_view) {


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val parentFragment = parentFragment ?: return
        val viewModel = ViewModelProviders.of(parentFragment).get(ParameterViewModel::class.java)
        viewModel.parameter.observe(this, Observer { parameter ->
            if (parameter != null) {
                initValues(parameter)
            }
        })
        val parameter = viewModel.parameter.value
        if (parameter != null) {
            initValues(parameter)
        }
    }

    private fun initValues(parameter: Parameter) {
        val values = parameter.measurements.map { measurement -> ChartData(measurement.logDate, measurement.value) }.sortedBy { data -> data.date }
        lineChartGraph.setLineColor(Color.parseColor(parameter.color))
        lineChartGraph.setDataSource(ArrayList(values))
    }
}