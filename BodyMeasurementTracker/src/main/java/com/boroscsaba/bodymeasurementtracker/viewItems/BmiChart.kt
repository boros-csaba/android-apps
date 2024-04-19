package com.boroscsaba.bodymeasurementtracker.viewItems

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class BmiChart: View {

    private var bmi: Float = 0f
    private var bmiString = "0"

    private var halfStroke = 0f
    private var canvasWidth = 0
    private var canvasHeight = 0
    private val chartElementsDistance = 0.5f

    private val extremelyUnderweightAngle = 5f
    private val underweightAngle = 50f
    private val normalAngle = 70f
    private val overweightAngle = 50f
    private val extremelyObeseAngle = 5f

    private val extremelyUnderweightLimit = 16f
    private val underweightLimit = 18.5f
    private val normalLimit = 25f
    private val overweightLimit = 40f

    private val extremelyUnderweightPaint = Paint()
    private val underweightPaint = Paint()
    private val normalPaint = Paint()
    private val overweightPaint = Paint()
    private val extremelyObesePaint = Paint()
    private val bmiLabelPaint = Paint()
    private val pinPaint = Paint()
    private val emptyPaint = Paint()
    private val textPaint = Paint()

    private val pinPath = Path()
    private val underweightArcPath = Path()
    private val normalArcPath = Path()
    private val overweightArcPath = Path()

    private val underweightLabelPath = Path()
    private val normalLabelPath = Path()
    private val overweightLabelPath = Path()
    private val extremelyOverweightLabelPath = Path()

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        initPaint(extremelyUnderweightPaint, "#002171")
        initPaint(underweightPaint, "#1976d2")
        initPaint(normalPaint, "#00c853")
        initPaint(overweightPaint, "#ff7043")
        initPaint(extremelyObesePaint, "#c63f17")
        initPaint(bmiLabelPaint, "#777777")
        initPaint(pinPaint, "#ffffff")
        initPaint(emptyPaint, "#ddffffff")
        initPaint(textPaint, "#555555")

        pinPaint.style = Paint.Style.FILL_AND_STROKE
        bmiString = "%.2f".format(bmi)
    }

    fun setHeightAndWeight(height: Double, heightUnit: String?, weight: Double, weightUnit: String?) {
        var h = height
        if (heightUnit == "in") h *= 2.54
        var w = weight
        if (weightUnit == "lbs") w *= 0.453592

        bmi = (w / (h * h) * 10000).toFloat()
        bmiString = "%.2f".format(bmi)
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 500
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> widthSize
            MeasureSpec.UNSPECIFIED -> desiredWidth
            else -> 0
        }
        var height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> width
            MeasureSpec.UNSPECIFIED -> width
            else -> 0
        }

        if (height * 2 > width) {
            height = width / 2
        }
        else {
            width = height * 2
        }

        canvasWidth = width
        canvasHeight = height * 2
        halfStroke = canvasWidth * 0.08f

        val stroke = halfStroke * 2
        extremelyUnderweightPaint.strokeWidth = stroke
        underweightPaint.strokeWidth = stroke
        normalPaint.strokeWidth = stroke
        overweightPaint.strokeWidth = stroke
        extremelyObesePaint.strokeWidth = stroke
        textPaint.textSize = canvasWidth * 0.06f
        textPaint.textAlign = Paint.Align.CENTER

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return
        drawChart(canvas)
        drawBmiText(canvas)
        if (bmi > 0f) {
            drawPin(canvas)
        }
        else {
            emptyPaint.style = Paint.Style.FILL
            canvas.drawRect(0f, 0f, canvasWidth.toFloat(), canvasHeight.toFloat(), emptyPaint)
            canvas.drawText("Weight and Height required!", canvasWidth / 2f, canvasHeight / 4f, textPaint)
        }
    }

    private fun drawChart(canvas: Canvas) {
        var startAngle = -180f
        canvas.drawArc(halfStroke, halfStroke, canvasWidth - halfStroke, canvasHeight - halfStroke, startAngle, extremelyUnderweightAngle - chartElementsDistance, false, extremelyUnderweightPaint)
        startAngle += extremelyUnderweightAngle
        canvas.drawArc(halfStroke, halfStroke, canvasWidth - halfStroke, canvasHeight - halfStroke, startAngle, underweightAngle - chartElementsDistance, false, underweightPaint)
        startAngle += underweightAngle
        canvas.drawArc(halfStroke, halfStroke, canvasWidth - halfStroke, canvasHeight - halfStroke, startAngle, normalAngle - chartElementsDistance, false, normalPaint)
        startAngle += normalAngle
        canvas.drawArc(halfStroke, halfStroke, canvasWidth - halfStroke, canvasHeight - halfStroke, startAngle, overweightAngle - chartElementsDistance, false, overweightPaint)
        startAngle += overweightAngle
        canvas.drawArc(halfStroke, halfStroke, canvasWidth - halfStroke, canvasHeight - halfStroke, startAngle, extremelyObeseAngle - chartElementsDistance, false, extremelyObesePaint)
    }

    private fun drawBmiText(canvas: Canvas) {
        val bmiNr = bmi
        val textPaint = when {
            bmiNr < extremelyUnderweightLimit -> extremelyUnderweightPaint
            bmiNr < underweightLimit -> underweightPaint
            bmiNr < normalLimit -> normalPaint
            bmiNr < overweightLimit -> overweightPaint
            else -> extremelyObesePaint
        }
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = canvasWidth * 0.09f
        textPaint.typeface = Typeface.DEFAULT_BOLD
        if (bmi <= 0) bmiString = "-.-"
        canvas.drawText(bmiString, canvasWidth / 2f, canvasHeight / 2.1f, textPaint)
        textPaint.style = Paint.Style.STROKE

        bmiLabelPaint.textAlign = Paint.Align.CENTER
        bmiLabelPaint.style = Paint.Style.FILL
        bmiLabelPaint.textSize = canvasWidth * 0.05f
        canvas.drawText("BMI", canvasWidth / 2f, canvasHeight / 2.5f, bmiLabelPaint)

        pinPaint.textSize = canvasWidth * 0.045f
        pinPaint.textAlign = Paint.Align.CENTER
        var offset = 20f
        var startAngle = -180f + extremelyUnderweightAngle
        underweightArcPath.addArc(halfStroke + offset, halfStroke + offset, canvasWidth - halfStroke - offset, canvasHeight - halfStroke  + offset, startAngle, underweightAngle)
        canvas.drawTextOnPath("Underweight".toCharArray(), 0, "Underweight".length, underweightArcPath, 0f, 0f, pinPaint)
        startAngle += underweightAngle
        normalArcPath.addArc(halfStroke + offset, halfStroke + offset, canvasWidth - halfStroke - offset, canvasHeight - halfStroke + offset, startAngle, normalAngle)
        canvas.drawTextOnPath("Normal".toCharArray(), 0, "Normal".length, normalArcPath, 0f, 0f, pinPaint)
        startAngle += normalAngle
        overweightArcPath.addArc(halfStroke + offset, halfStroke + offset, canvasWidth - halfStroke - offset, canvasHeight - halfStroke + offset, startAngle, overweightAngle)
        canvas.drawTextOnPath("Overweight".toCharArray(), 0, "Overweight".length, overweightArcPath, 0f, 0f, pinPaint)

        bmiLabelPaint.textSize = canvasWidth * 0.03f
        offset = 37f
        startAngle = -180f + extremelyUnderweightAngle
        var text = "%.1f".format(extremelyUnderweightLimit)
        underweightLabelPath.addArc(2 * halfStroke + offset, 2 * halfStroke + offset, canvasWidth -  2 * halfStroke - offset, canvasHeight - 2 * halfStroke + offset, startAngle - 5, 20f)
        canvas.drawTextOnPath(text.toCharArray(), 0, text.length, underweightLabelPath, 0f, 0f, bmiLabelPaint)
        startAngle += underweightAngle
        text = "%.1f".format(underweightLimit)
        normalLabelPath.addArc(2 * halfStroke + offset, 2 * halfStroke + offset, canvasWidth -  2 * halfStroke - offset, canvasHeight - 2 * halfStroke + offset, startAngle - 10, 20f)
        canvas.drawTextOnPath(text.toCharArray(), 0, text.length, normalLabelPath, 0f, 0f, bmiLabelPaint)
        startAngle += normalAngle
        text = "%.1f".format(normalLimit)
        overweightLabelPath.addArc(2 * halfStroke + offset, 2 * halfStroke + offset, canvasWidth -  2 * halfStroke - offset, canvasHeight - 2 * halfStroke + offset, startAngle - 10, 20f)
        canvas.drawTextOnPath(text.toCharArray(), 0, text.length, overweightLabelPath, 0f, 0f, bmiLabelPaint)
        startAngle += overweightAngle
        text = "%.1f".format(overweightLimit)
        extremelyOverweightLabelPath.addArc(2 * halfStroke + offset, 2 * halfStroke + offset, canvasWidth -  2 * halfStroke - offset, canvasHeight - 2 * halfStroke + offset, startAngle - 15, 20f)
        canvas.drawTextOnPath(text.toCharArray(), 0, text.length, extremelyOverweightLabelPath, 0f, 0f, bmiLabelPaint)
    }

    private fun drawPin(canvas: Canvas) {
        val bmiNr = bmi
        val angleDegree = 180 + when {
            bmiNr < extremelyUnderweightLimit -> -185f
            bmiNr < underweightLimit -> {
                val startingAngle = -180 + extremelyUnderweightAngle
                (1 - (underweightLimit - bmiNr) / (underweightLimit - extremelyUnderweightLimit)) * underweightAngle + startingAngle
            }
            bmiNr < normalLimit -> {
                val startingAngle = -180 + extremelyUnderweightAngle + underweightAngle
                (1 - (normalLimit - bmiNr) / (normalLimit - underweightLimit)) * normalAngle + startingAngle
            }
            bmiNr < overweightLimit -> {
                val startingAngle = -180 + extremelyUnderweightAngle + underweightAngle + normalAngle
                (1 - (overweightLimit - bmiNr) / (overweightLimit - normalLimit)) * overweightAngle + startingAngle
            }
            else -> 5f
        }

        val pinOpening = 0.08f
        val angle: Double = (180 - angleDegree) * PI / 180

        pinPath.reset()
        pinPath.fillType = Path.FillType.EVEN_ODD

        val x1 = cos(angle).toFloat() * (canvasHeight / 2 - halfStroke * 1.5f) + canvasHeight / 2
        val y1 = canvasHeight / 2 - sin(angle).toFloat() * (canvasHeight / 2 - halfStroke * 1.5f)

        val x2 = cos(angle + pinOpening).toFloat() * (canvasHeight / 2 - halfStroke * 2) + canvasHeight / 2
        val y2 = canvasHeight / 2 - sin(angle + pinOpening).toFloat() * (canvasHeight / 2 - halfStroke * 2)

        val x3 = cos(angle - pinOpening).toFloat() * (canvasHeight / 2 - halfStroke * 2) + canvasHeight / 2
        val y3 = canvasHeight / 2 - sin(angle - pinOpening).toFloat() * (canvasHeight / 2 - halfStroke * 2)


        pinPath.moveTo(x1, y1)
        pinPath.lineTo(x2, y2)
        pinPath.lineTo(x3, y3)
        pinPath.close()

        pinPaint.style = Paint.Style.FILL_AND_STROKE
        canvas.drawPath(pinPath, pinPaint)
    }

    private fun initPaint(paint: Paint, color: String) {
        paint.isAntiAlias = true
        paint.color = Color.parseColor(color)
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
    }
}