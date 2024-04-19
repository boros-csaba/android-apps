package com.boroscsaba.commonlibrary.viewelements.charts

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.view.LayoutInflater
import android.view.Gravity
import android.view.View
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.ThemeManager


class LineChart: LinearLayout {

    private var viewRoot: View? = null

    constructor(context: Context): super(context) { init(null) }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) { init(attrs) }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) { init(attrs) }

    private fun init(attrs: AttributeSet?) {
        orientation = VERTICAL
        gravity = Gravity.CENTER_VERTICAL

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        viewRoot = inflater.inflate(R.layout.line_chart, this, true)

        val themeManager = (context.applicationContext as ApplicationBase).themeManager
        var styleName: String? = null

        if (attrs != null) {
            val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.LineChart)
            styleName = styledAttributes.getString(R.styleable.LineChart_styleName)
            styledAttributes.recycle()
        }
        setLineColor(themeManager.getColor(ThemeManager.LINE_CHART_LINE_COLOR, styleName))
    }

    fun setDataSource(inputData: ArrayList<ChartData>) {
        val chart = viewRoot?.findViewById<LineChartGraph>(R.id.lineChartGraph) ?: return
        chart.setDataSource(inputData)
    }

    fun setLineColor(colorCode: Int) {
        val chart = viewRoot?.findViewById<LineChartGraph>(R.id.lineChartGraph) ?: return
        chart.setLineColor(colorCode)
    }
}
