package com.boroscsaba.commonlibrary.activities.historyView

import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.viewelements.charts.ChartData
import com.boroscsaba.dataaccess.EntityBase
import kotlinx.android.synthetic.main.base_layout_history_line_chart.view.*

class LineChartComponent<T:EntityBase>(chartDataMapper: (T) -> ChartData): Component<T>(R.layout.base_layout_history_line_chart, { viewHolder, _, _, _, _, items ->
    val chartData = ArrayList(items.map{ item -> chartDataMapper(item) })
    viewHolder.itemView.lineChart.setDataSource(chartData)
})