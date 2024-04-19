package com.boroscsaba.commonlibrary.viewelements.charts

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.getSize
import androidx.core.graphics.ColorUtils
import com.boroscsaba.commonlibrary.Utils
import java.text.SimpleDateFormat
import kotlin.math.min

class LineChartGraph: View {

    private var canvasWidth = 0
    private var canvasHeight = 0
    private var canvasChartHeight = 0
    private var canvasChartTopMargin = 0
    private val data = ArrayList<ChartData>()
    private val points = ArrayList<Point>()
    private var numberOfDays: Long = 1
    private var firstDay: Long = 0
    private var lastDate: Long = 0
    private var minValue = 0.0
    private var maxValue = 1.0
    private var gridStep = 0.0f
    private var dataDifferenceMagnitude = 0.0
    private var firstGridLabel = 0.0
    private var pointRadius = 0f
    private var leftPadding = 0f
    private var rightPadding = 0f
    private var dateLabelDaysGap = 0
    private var horizontalDayGap = 0f
    private var bottomDateLabelsPadding = 0f
    private var showLabels = true

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    private val dayInMilliseconds: Long = 1000 * 60 * 60 * 24
    private val verticalPaddingPercentage = 10
    private val maxGridLinesCount = 5
    private val maxGridDateLabelsCount = 7
    private val gridStrokeWidth = 2f

    private val linePaint = Paint()
    private val pointPaint = Paint()
    private val gridPaint = Paint()
    private val gridLabelPaint = Paint()
    private val emptyChartTextPaint = Paint()
    private val gradientPaint = Paint()
    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("MMM d")

    init {
        initPaint(linePaint, "#000000")
        initPaint(pointPaint, "#3700B3")
        initPaint(gridPaint, "#bbbbbb")
        initPaint(gridLabelPaint, "#555555")
        initPaint(emptyChartTextPaint, "#777777")
        initPaint(gradientPaint, "#000000")

        gridPaint.strokeWidth = gridStrokeWidth
        emptyChartTextPaint.textAlign = Paint.Align.CENTER
        emptyChartTextPaint.style = Paint.Style.FILL
        gridLabelPaint.textAlign = Paint.Align.CENTER
        gridLabelPaint.style = Paint.Style.FILL
    }

    fun setDataSource(inputData: ArrayList<ChartData>) {
        data.clear()
        data.addAll(inputData)
        if (data.count() < 2) {
            if (data.isEmpty()) {
                data.add(ChartData(System.currentTimeMillis(), 1.0))
            }
            data.add(ChartData(data[0].date - 1000 * 60 * 60 * 24, data[0].value))
            data.add(ChartData(data[0].date + 1000 * 60 * 60 * 24, data[0].value))
        }
        data.sortBy { d -> d.date }

        firstDay = Utils.getDayNrFromDate(data.first().date)
        lastDate = data.last().date
        val lastDay = Utils.getDayNrFromDate(data.last().date)
        numberOfDays = lastDay - firstDay

        val minRealValue = data.minBy { d -> d.value }?.value ?: 0.0
        val maxRealValue = data.maxBy { d -> d.value }?.value ?: 1.0
        var valueOffset = (maxRealValue - minRealValue) * verticalPaddingPercentage / 100
        if (valueOffset == 0.0) valueOffset = 10.0

        minValue = minRealValue - valueOffset
        if (minValue < 0) minValue = 0.0
        maxValue = maxRealValue + valueOffset

        recalculateDataPoints()

        invalidate()
    }

    fun setLineColor(colorCode: Int) {
        linePaint.color = colorCode
    }

    fun hideAllLabels() {
        showLabels = false
    }

    fun setGridColor(colorCode: Int) {
        gridPaint.color = colorCode
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        canvasWidth = getSize(widthMeasureSpec)
        canvasHeight = min(Math.round(canvasWidth * 0.75).toInt(), getSize(heightMeasureSpec))

        bottomDateLabelsPadding = Math.max(canvasHeight / 20, 20).toFloat()
        canvasChartHeight = canvasHeight - Math.max(canvasHeight / 9, 50)
        canvasChartTopMargin = (gridLabelPaint.measureText("0") / 3).toInt()

        setMeasuredDimension(canvasWidth, canvasHeight)

        linePaint.strokeWidth = canvasWidth * 0.007f
        pointRadius = canvasWidth * 0.001f
        emptyChartTextPaint.textSize = canvasWidth * 0.05f
        gridLabelPaint.textSize = canvasHeight * 0.05f

        recalculateDataPoints()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return

        drawGrid(canvas)
        drawData(canvas)
    }

    private fun drawGrid(canvas: Canvas) {
        var labelValue = firstGridLabel
        while (labelValue >= minValue) {
            val y = canvasChartHeight + canvasChartTopMargin - ((labelValue - minValue) / (maxValue - minValue) * canvasChartHeight).toFloat()
            canvas.drawLine(leftPadding, y, canvasWidth.toFloat(), y, gridPaint)
            if (showLabels) {
                canvas.save()
                canvas.rotate(-25f, leftPadding / 2, y)
                canvas.drawText(getLabelText(labelValue), leftPadding / 2, y, gridLabelPaint)
                canvas.restore()
            }
            labelValue -= gridStep
        }

        val numberOfDatesToDisplay = Math.ceil(numberOfDays.toDouble() / dateLabelDaysGap.toDouble()).toInt()
        for (i in 0 until numberOfDatesToDisplay) {
            val gridDate = lastDate - (dayInMilliseconds * i * dateLabelDaysGap)
            val date: Long = if (i == numberOfDatesToDisplay) {
                lastDate - (dayInMilliseconds * numberOfDays)
            }
            else {
                lastDate - (dayInMilliseconds * i * dateLabelDaysGap)
            }
            if (showLabels) {
                canvas.drawText(dateFormat.format(date), (Utils.getDayNrFromDate(date) - firstDay) * horizontalDayGap + leftPadding, canvasHeight.toFloat() - bottomDateLabelsPadding, gridLabelPaint)
            }
            canvas.drawLine((Utils.getDayNrFromDate(gridDate) - firstDay) * horizontalDayGap + leftPadding, 0f, (Utils.getDayNrFromDate(date) - firstDay) * horizontalDayGap + leftPadding, canvasChartHeight.toFloat(), gridPaint)
        }

    }

    private fun drawData(canvas: Canvas) {
        val gradientColor = ColorUtils.setAlphaComponent(linePaint.color, 35)
        gradientPaint.shader = LinearGradient(0f, 0f, 0f, canvasChartHeight.toFloat(),
                arrayOf(gradientColor, Color.TRANSPARENT).toIntArray(), null, Shader.TileMode.CLAMP)
        gradientPaint.isDither = true

        for (i in points.indices) {
            if (i > 0) {
                canvas.drawLine(points[i - 1].x, points[i - 1].y, points[i].x, points[i].y, linePaint)

                val path = Path()
                path.moveTo(points[i - 1].x, points[i -1].y)
                path.lineTo(points[i].x, points[i].y)
                path.lineTo(points[i].x, canvasChartHeight.toFloat())
                path.lineTo(points[i - 1].x, canvasChartHeight.toFloat())
                path.lineTo(points[i - 1].x, points[i -1].y)
                canvas.drawPath(path, gradientPaint)
            }
        }
        for (i in points.indices) {
            //canvas.drawCircle(points[i].x, points[i].y, pointRadius, pointPaint)
        }
    }

    private fun recalculateDataPoints() {
        if (canvasWidth == 0) return
        points.clear()

        leftPadding = gridLabelPaint.measureText(getLabelText(maxValue)) * 1.1f
        rightPadding = 20f

        horizontalDayGap = (canvasWidth - leftPadding - rightPadding) / numberOfDays
        val difference = maxValue - minValue

        data.forEach { d ->
            val point = Point()
            point.x = (Utils.getDayNrFromDate(d.date) - firstDay) * horizontalDayGap + leftPadding
            point.y = (canvasChartHeight - ((d.value - minValue) / difference * canvasChartHeight)).toFloat()
            points.add(point)
        }

        var differenceMagnitude = 0.001
        while (difference / differenceMagnitude >=  10) {
            differenceMagnitude *= 10
        }
        differenceMagnitude /= 10
        dataDifferenceMagnitude = differenceMagnitude
        gridStep = differenceMagnitude.toFloat()
        while (difference / gridStep > maxGridLinesCount) {
            gridStep *= 2
            if (gridStep == 0.04f) gridStep = 0.05f
            if (gridStep == 0.4f) gridStep = 0.5f
            if (gridStep == 4f) gridStep = 5f
            if (gridStep == 40f) gridStep = 50f
            if (gridStep == 400f) gridStep = 500f
            if (gridStep == 4000f) gridStep = 5000f
        }
        firstGridLabel = maxValue - (maxValue % gridStep)

        dateLabelDaysGap = Math.ceil(numberOfDays.toDouble() / maxGridDateLabelsCount.toDouble()).toInt()
    }

    private fun initPaint(paint: Paint, color: String) {
        paint.isAntiAlias = true
        paint.color = Color.parseColor(color)
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeJoin = Paint.Join.ROUND
    }

    private fun getLabelText(value: Double): String {
        return String.format("%.1f", value)
    }

    class Point {
        var x: Float = 0f
        var y: Float = 0f
    }
}