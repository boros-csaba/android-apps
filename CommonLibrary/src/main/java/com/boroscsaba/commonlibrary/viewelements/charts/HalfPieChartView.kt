package com.boroscsaba.commonlibrary.viewelements.charts

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.ThemeManager
import java.text.DecimalFormat

/**
* Created by boros on 1/22/2018.
*/
class HalfPieChartView : View {

    private var text = "0%"
    private var percentage = 0f
    private var halfStroke = 0f
    private var canvasWidth = 0
    private var canvasHeight = 0
    private var emptyArcPaint = Paint()
    private var fullArcPaint = Paint()
    private var textPaint = Paint()

    constructor(context: Context): super(context) { init() }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) { init() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) { init() }

    private fun init() {
        val themeManager = (context.applicationContext as ApplicationBase).themeManager

        emptyArcPaint.isAntiAlias = true
        emptyArcPaint.color = Color.parseColor("#dddddd")
        emptyArcPaint.style = Paint.Style.STROKE
        emptyArcPaint.strokeJoin = Paint.Join.ROUND

        fullArcPaint.isAntiAlias = true
        fullArcPaint.color = Color.parseColor("#83b400")
        fullArcPaint.style = Paint.Style.STROKE
        fullArcPaint.strokeJoin = Paint.Join.ROUND

        textPaint.isAntiAlias = true
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.style = Paint.Style.FILL
        textPaint.color = themeManager.getColor(ThemeManager.PRIMARY_TEXT_COLOR)
    }

    fun setPercentage(percentage: Float) {
        this.percentage = percentage
        val decimalFormat = DecimalFormat("0.##")
        text = decimalFormat.format(percentage) + "%"
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val maxWidth = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        canvasWidth = maxWidth
        canvasHeight = canvasWidth
        halfStroke = canvasWidth * 0.1f
        emptyArcPaint.strokeWidth = halfStroke * 2
        fullArcPaint.strokeWidth = halfStroke * 2
        textPaint.textSize = canvasWidth * 0.1f

        setMeasuredDimension(canvasWidth, (canvasWidth / 2.4f).toInt())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return
        canvas.drawArc(halfStroke, halfStroke,canvasWidth - halfStroke, canvasHeight - halfStroke, -160f, 140f, false, emptyArcPaint)
        canvas.drawArc(halfStroke, halfStroke, canvasWidth - halfStroke, canvasHeight - halfStroke, -160f, percentage / 100 * 140, false, fullArcPaint)
        canvas.drawText(text, canvasWidth / 2f, canvasHeight / 2.5f, textPaint)
    }
}