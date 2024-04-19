package com.boroscsaba.commonlibrary.viewelements.charts

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.ThemeManager
import com.boroscsaba.commonlibrary.Utils
import java.util.*

class BarChart: View {

    constructor(context: Context): super(context) { init(null) }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) { init(attrs) }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) { init(attrs) }

    private val aspectRatio = 1.2f
    private val chartTopRatio = 0.3f
    private val maxNrOfBars = 15
    private val barGapRatio = 0.30f
    private val dateLabelsTextScale = 0.03f
    private val monthLabelTextScale = 0.04f
    private val noDataTextScale = 0.06f

    private val data = ArrayList<ChartData>()
    private var valuePatter: (value: Double) -> String = { "" }
    private var isEmpty = true

    private val barPaint = Paint()
    private val emptyBarPaint = Paint()
    private val dateLabelsPaint = Paint()
    private val valueLabelsPaint = Paint()
    private val monthLabelPaint = Paint()
    private val noDataTextPaint = Paint()
    private val transparentPaint = Paint()
    private var barWidth = 0f
    private var chartAreaStart = 0f
    private var chartAreaHeight = 0f
    private var barGapWidth = 0f
    private var firstDay: Long = 0
    private var lastDay: Long = 0
    private var maxValue = 0.0
    private var averageValue = 0.0

    private fun init(attrs: AttributeSet?) {
        barPaint.color = Color.parseColor("#b63f36")
        emptyBarPaint.color = Color.parseColor("#dddddd")
        dateLabelsPaint.isAntiAlias = true
        dateLabelsPaint.textAlign = Paint.Align.CENTER
        Color.parseColor("#555555")
        valueLabelsPaint.isAntiAlias = true
        valueLabelsPaint.textAlign = Paint.Align.LEFT
        monthLabelPaint.color = Color.parseColor("#555555")
        monthLabelPaint.isAntiAlias = true
        monthLabelPaint.textAlign = Paint.Align.LEFT
        noDataTextPaint.color = Color.parseColor("#555555")
        noDataTextPaint.isAntiAlias = true
        noDataTextPaint.textAlign = Paint.Align.CENTER
        transparentPaint.color = Color.parseColor("#55cccccc")

        val themeManager = (context.applicationContext as ApplicationBase).themeManager
        var styleName: String? = null

        if (attrs != null) {
            val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.BarChart)
            styleName = styledAttributes.getString(R.styleable.BarChart_styleName)
            styledAttributes.recycle()
        }
        dateLabelsPaint.color = themeManager.getColor(ThemeManager.BAR_CHART_LABELS_COLOR, styleName)
        valueLabelsPaint.color = dateLabelsPaint.color
    }

    fun setDataSource(inputData: ArrayList<ChartData>, valuePatter: (value: Double) -> String) {
        this.valuePatter = valuePatter
        data.clear()
        data.addAll(inputData)
        if (inputData.size == 0) {
            isEmpty = true
            for (i in 0..30) {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.HOUR, i * -24)
                if ((0..100).random() > 30) {
                    data.add(ChartData(calendar.timeInMillis, (0..100).random().toDouble()))
                }
            }
        }
        else {
            isEmpty = false
        }
        if (data.isNotEmpty()) {
            data.sortBy { d -> d.date }

            firstDay = Utils.getDayNrFromDate(data.first().date)
            lastDay = Utils.getDayNrFromDate(System.currentTimeMillis())
            if (lastDay - firstDay < maxNrOfBars) {
                firstDay = lastDay - maxNrOfBars
            }
            maxValue = data.maxBy { d -> d.value }?.value ?: 1.0
            averageValue = data.sumByDouble { d -> d.value } / data.count()
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        var monthLabelDrawn = false
        var lastPosition = width.toFloat() - barGapWidth
        for (dayNr in lastDay downTo firstDay) {
            val dataPoint = data.firstOrNull { d -> Utils.getDayNrFromDate(d.date) == dayNr }
            if (dataPoint != null) {
                canvas.drawRect(
                        lastPosition - barWidth,
                        (chartAreaHeight - (dataPoint.value / maxValue * chartAreaHeight) + chartAreaStart).toFloat(),
                        lastPosition,
                        chartAreaStart + chartAreaHeight,
                        barPaint)

                val amountLabel = valuePatter(dataPoint.value)
                canvas.save()
                val textX = lastPosition - barWidth / 2 + 1
                val textY = (chartAreaHeight - (dataPoint.value / maxValue * chartAreaHeight) + chartAreaStart * 0.9).toFloat()
                canvas.rotate(-55f, textX, textY)
                if (!isEmpty) {
                    canvas.drawText(amountLabel, textX, textY, valueLabelsPaint)
                }
                canvas.restore()
            }
            else {
                canvas.drawRect(
                        lastPosition - barWidth,
                        (chartAreaHeight - (averageValue / maxValue * chartAreaHeight) + chartAreaStart).toFloat(),
                        lastPosition,
                        chartAreaStart + chartAreaHeight,
                        emptyBarPaint)
            }

            val dayOfMonth = Utils.getDayOfMonth(dayNr * 1000 * 60 * 60 * 24)
            canvas.drawText(dayOfMonth.toString(), lastPosition - barWidth / 2 + 1, height - monthLabelPaint.textSize * 1.5f, dateLabelsPaint)

            if (dayOfMonth == 1) {
                monthLabelDrawn = true
                monthLabelPaint.textAlign = Paint.Align.LEFT
                val posX = lastPosition - barWidth - barGapWidth / 2
                canvas.drawLine(posX - 1, height - monthLabelPaint.textSize * 1.5f, posX + 1, height - monthLabelPaint.textSize * 0.1f, monthLabelPaint)
                canvas.drawText(Utils.getMonthName(context,dayNr * 1000 * 60 * 60 * 24), lastPosition - barWidth / 2, height - monthLabelPaint.textSize * 0.5f, monthLabelPaint)
            }
            else if (monthLabelDrawn) {
                monthLabelDrawn = false
                monthLabelPaint.textAlign = Paint.Align.RIGHT
                canvas.drawText(Utils.getMonthName(context,dayNr * 1000 * 60 * 60 * 24), lastPosition - barWidth / 2, height - monthLabelPaint.textSize * 0.5f, monthLabelPaint)
            }

            lastPosition -= barWidth + barGapWidth
        }
        if (isEmpty) {
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), transparentPaint)
            canvas.drawText("NO DATA YET", (width / 2).toFloat(), (height / 3).toFloat(), noDataTextPaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 500
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> widthSize
            MeasureSpec.UNSPECIFIED -> desiredWidth
            else -> 0
        }

        val height = width / aspectRatio
        chartAreaStart = height * chartTopRatio
        val barTotalWidth = width / (maxNrOfBars - barGapRatio)
        barGapWidth = barTotalWidth * barGapRatio
        barWidth = barTotalWidth - barGapWidth
        dateLabelsPaint.textSize = width * dateLabelsTextScale
        valueLabelsPaint.textSize = width * dateLabelsTextScale
        monthLabelPaint.textSize = width * monthLabelTextScale
        noDataTextPaint.textSize = width * noDataTextScale
        chartAreaHeight = height - dateLabelsPaint.textSize * 1.2f - chartAreaStart - monthLabelPaint.textSize * 1.5f

        setMeasuredDimension(width, height.toInt())
    }
}