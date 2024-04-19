package com.boroscsaba.commonlibrary.viewelements.charts

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.text.DecimalFormat

class RoundProgressChart(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var chartSize = 0
    private var horizontalPadding = 0
    private var verticalPadding = 0
    private var chartInnerSize = 0

    private val chartPaint = Paint()
    private val backgroundPaint = Paint()
    private var gradient: SweepGradient? = null
    private var gradientColors = IntArray(3)
    private val decimalFormat = DecimalFormat("0.##")
    private var percentage = 0.0
    private val textPaint = Paint()
    private val emptyChartPaint = Paint()

    init {
        backgroundPaint.color = Color.parseColor("#fffafafa")
        backgroundPaint.isAntiAlias = true
        chartPaint.isAntiAlias = true
        textPaint.color = Color.parseColor("#777777")
        textPaint.isAntiAlias = true
        textPaint.textAlign = Paint.Align.CENTER
        emptyChartPaint.color = Color.parseColor("#dddddd")
        emptyChartPaint.isAntiAlias = true
        gradientColors[0] = Color.parseColor("#f02f31")
        gradientColors[1] = Color.parseColor("#ffeb47")
        gradientColors[2] = Color.parseColor("#5bbd4c")
    }

    fun setPercentage(percentage: Double) {
        this.percentage = percentage
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (gradient == null) {
            gradient = SweepGradient(width / 2f, height / 2f, gradientColors, null)
            chartPaint.shader = gradient
        }

        canvas.drawCircle(width / 2f + horizontalPadding, height / 2f + verticalPadding, chartSize / 2f, emptyChartPaint)
        canvas.save()
        canvas.rotate(-90f, width / 2f, height / 2f)
        canvas.drawArc(horizontalPadding.toFloat(), verticalPadding.toFloat(), width - horizontalPadding.toFloat(), height - verticalPadding.toFloat(), 0f, (360 * percentage / 100f).toFloat(), true, chartPaint)
        canvas.restore()
        canvas.drawCircle(width / 2f + horizontalPadding, height / 2f + verticalPadding, chartInnerSize / 2f, backgroundPaint)
        canvas.drawText(decimalFormat.format(percentage) + "%", width / 2f + horizontalPadding, height / 2f + verticalPadding + textPaint.textSize / 2, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 500
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            View.MeasureSpec.EXACTLY -> widthSize
            View.MeasureSpec.AT_MOST -> Math.min(desiredWidth, widthSize)
            View.MeasureSpec.UNSPECIFIED -> desiredWidth
            else -> 0
        }
        val height = when (heightMode) {
            View.MeasureSpec.EXACTLY -> heightSize
            View.MeasureSpec.AT_MOST -> Math.min(width, heightSize)
            View.MeasureSpec.UNSPECIFIED -> width
            else -> 0
        }

        chartSize = Math.min(width, height)
        horizontalPadding = (width - chartSize) / 2
        verticalPadding = (width - chartSize) / 2
        chartInnerSize = (chartSize * 0.7f).toInt()
        textPaint.textSize = chartSize * 0.1f
        setMeasuredDimension(width, height)
    }
}