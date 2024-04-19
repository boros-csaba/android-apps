package com.boroscsaba.fastinglifestyletimerandtracker

import android.content.Context
import android.graphics.*
import android.graphics.drawable.RotateDrawable
import android.util.AttributeSet
import android.view.View
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.ThemeManager
import java.util.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class Timer(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    var onStopListener: (() -> Unit)? = null
    var onStartListener: ((Long, Int, Int) -> Unit)? = null
    var updateTimerText: ((timeText: String, isOver: Boolean) -> Unit)? = null

    var timerStartDate: Long = 0
    var timerTargetEndDate: Long = 0
    private var running = false
    private var percentage = 75.1f
    private var targetDuration: Float = 0f
    private var targetHours = 0
    private var targetMinutes = 0

    private val nrOfDashes = 12
    private val nrOfSmallDashes = 60
    private val buttonsVerticalMargin = 0.2f
    private val dashDistanceAngleRadians = 2 * Math.PI / nrOfDashes
    private val smallDashDistanceAngleRadians = 2 * Math.PI / nrOfSmallDashes
    private val dashPaint = Paint()
    private val smallDashPaint = Paint()
    private val dashMarkerPaint = Paint()
    private val dashMarkerOverPaint = Paint()
    private var backgroundPaint = Paint()
    private val textPaint = Paint()
    private val timerTextPaint = Paint()
    private val whitePaint = Paint()
    private val hoursButtonBorderPaint = Paint()
    private val linesBackgroundPaint = Paint()
    private var forkDrawable: RotateDrawable = RotateDrawable()
    private var knifeDrawable: RotateDrawable = RotateDrawable()
    private val calendar = Calendar.getInstance()
    private val pulsePaint = Paint()

    private var timerSize = 0f
    private var horizontalPadding = 0f
    private var verticalPadding = 0f
    private var timerDashWidth = 0f
    private var timerCenterX = 0f
    private var timerCenterY = 0f
    private var startButtonCenterX = 0f
    private var buttonsCenterY = 0f
    private var hoursButtonCenterY = 0f
    private var hoursButtonRadius = 0f
    private var buttonsRadius = 0f

    init {
        initPaints()
    }

    fun startTimer(targetHours: Int, targetMinutes: Int) {
        timerStartDate = System.currentTimeMillis()
        timerTargetEndDate = (timerStartDate + 1000 * 60 * 60 * targetDuration).toLong()
        this.targetHours = targetHours
        this.targetMinutes = targetMinutes
        targetDuration = targetHours + targetMinutes / 60f
        if (!running) {
            onStartListener?.invoke(timerStartDate, targetHours, targetMinutes)
        }
        running = true
    }

    fun resumeTimer(startDate: Long, targetHours: Int, targetMinutes: Int) {
        timerStartDate = startDate
        timerTargetEndDate = timerStartDate + (targetHours * 60 + targetMinutes) * 60 * 1000
        this.targetHours = targetHours
        this.targetMinutes = targetMinutes
        targetDuration = targetHours + targetMinutes / 60f
        running = true
    }

    fun stopTimer() {
        val onStopListener = this.onStopListener
        if (running && onStopListener != null) onStopListener.invoke()
        running = false
    }

    private fun initPaints() {
        dashPaint.color = Color.parseColor("#ffffff")
        dashPaint.isAntiAlias = true
        smallDashPaint.color = Color.parseColor("#ffffff")
        smallDashPaint.isAntiAlias = true
        dashMarkerPaint.color = Color.parseColor("#ffffff")
        dashMarkerPaint.isAntiAlias = true
        dashMarkerOverPaint.isAntiAlias = true
        textPaint.color = Color.parseColor("#ffffff")

        timerTextPaint.color = Color.parseColor("#ffffff")
        timerTextPaint.textAlign = Paint.Align.CENTER
        whitePaint.color = Color.parseColor("#ffffff")
        whitePaint.textAlign = Paint.Align.CENTER

        linesBackgroundPaint.color = Color.parseColor("#ffffff")
        hoursButtonBorderPaint.color = Color.parseColor("#ffffff")
        pulsePaint.color = Color.parseColor("#ffffff")
        forkDrawable.drawable = resources.getDrawable(R.drawable.ic_fork, null)
        knifeDrawable.drawable = resources.getDrawable(R.drawable.ic_knife, null)

        val themeManager = (context.applicationContext as ApplicationBase).themeManager
        backgroundPaint.color = themeManager.getColor(ThemeManager.BACKGROUND_COLOR, "TIMER_PAGE")
        if (ThemeManager.isDarkMode) {
            dashMarkerOverPaint.color = Color.parseColor("#500000")
        }
        else {
            dashMarkerOverPaint.color = Color.parseColor("#b53d2a")
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
        drawTimerLines(canvas)
        drawClockHands(canvas)
        if (running) {
            updateTimerText()
            drawPulse(canvas)
        }
        invalidate()
    }

    private fun drawTimerLines(canvas: Canvas) {
        if (running) {
            percentage = 100 - ((timerTargetEndDate - System.currentTimeMillis()) * 100 / (targetDuration * 1000 * 60 * 60))
            val startAngle = (getHours(timerStartDate) / 12f * Math.PI * 2f) - Math.PI / 2f

            canvas.drawArc(horizontalPadding, verticalPadding, horizontalPadding + timerSize, verticalPadding + timerSize,
                    (startAngle * 180 / Math.PI).toFloat(),
                    percentage * 3.6f * targetDuration / 12f,
                    true, if (percentage > 100) dashMarkerOverPaint else dashMarkerPaint)
            val arcSize = 0.1f
            canvas.drawArc(horizontalPadding + timerSize * arcSize , verticalPadding + timerSize * arcSize, horizontalPadding + timerSize - timerSize * arcSize, verticalPadding + timerSize - timerSize * arcSize, 0f, 360f, true, backgroundPaint)

            drawDashMarker(startAngle, canvas)
            val endAngle = (getHours(timerTargetEndDate) / 12 * Math.PI * 2) - Math.PI / 2
            drawDashMarker(endAngle, canvas)
        }

        canvas.save()
        canvas.rotate(-90f, timerSize / 2f + horizontalPadding, timerSize / 2f + verticalPadding)
        var angle = 0.0
        for (i: Int in 1..nrOfDashes) {
            val cos = cos(angle).toFloat()
            val sin = sin(angle).toFloat()
            canvas.drawLine(
                    timerSize / 2f * cos * 0.9f + timerSize / 2f + horizontalPadding,
                    timerSize / 2f * sin * 0.9f + timerSize / 2f + verticalPadding,
                    timerSize / 2f * cos + timerSize / 2f + horizontalPadding,
                    timerSize / 2f * sin + timerSize / 2f + verticalPadding,
                    dashPaint)
            angle += dashDistanceAngleRadians
        }
        canvas.restore()

        canvas.save()
        canvas.rotate(-90f, timerSize / 2f + horizontalPadding, timerSize / 2f + verticalPadding)
        angle = 0.0
        for (i: Int in 1..nrOfSmallDashes) {
            val cos = cos(angle).toFloat()
            val sin = sin(angle).toFloat()
            canvas.drawLine(
                    timerSize / 2f * cos * 0.95f + timerSize / 2f + horizontalPadding,
                    timerSize / 2f * sin * 0.95f + timerSize / 2f + verticalPadding,
                    timerSize / 2f * cos + timerSize / 2f + horizontalPadding,
                    timerSize / 2f * sin + timerSize / 2f + verticalPadding,
                    smallDashPaint)
            angle += smallDashDistanceAngleRadians
        }
        canvas.restore()
    }

    private var lastPulse: Long = 0
    private var lastPulseIndex = 0
    private var pulseSize = arrayOf(0f, 0f, 0f)
    private fun drawPulse(canvas: Canvas) {
        if (System.currentTimeMillis() - lastPulse > 1000) {
            lastPulseIndex++
            if (lastPulseIndex >= pulseSize.size) {
                lastPulseIndex = 0
            }
            pulseSize[lastPulseIndex] = 30f
            lastPulse = System.currentTimeMillis()
        }
        for (pulse in 0 until pulseSize.size) {
            if (pulseSize[pulse] > 0) {
                pulsePaint.alpha = min((((1 - (pulseSize[pulse] / (timerSize / 2f))) * 255).toInt()), 30)
                canvas.drawCircle(timerCenterX, timerCenterY, pulseSize[pulse], pulsePaint)
                pulseSize[pulse] = pulseSize[pulse] + 1.5f
                if (pulseSize[pulse] > timerSize / 2f) {
                    pulseSize[pulse] = 0f
                }
            }
        }
    }

    private var hoursHandSize = 0f
    private fun drawClockHands(canvas: Canvas) {
        forkDrawable.fromDegrees = 0f
        forkDrawable.toDegrees = 360f
        forkDrawable.setBounds(
                (timerCenterX - hoursHandSize / 2f).toInt(),
                (timerCenterY - hoursHandSize * 0.95f).toInt(),
                (timerCenterX + hoursHandSize / 2f).toInt(),
                (timerCenterY + hoursHandSize * 0.05f).toInt())
        forkDrawable.isPivotXRelative = true
        forkDrawable.pivotX = 0.5f
        forkDrawable.isPivotYRelative = true
        forkDrawable.pivotY = 0.95f

        knifeDrawable.fromDegrees = 0f
        knifeDrawable.toDegrees = 360f
        knifeDrawable.setBounds(
                (timerCenterX - hoursHandSize * 0.9f / 2f).toInt(),
                (timerCenterY - hoursHandSize * 0.9f * 0.95f).toInt(),
                (timerCenterX + hoursHandSize * 0.9f / 2f).toInt(),
                (timerCenterY + hoursHandSize * 0.9f * 0.05f).toInt())
        knifeDrawable.isPivotXRelative = true
        knifeDrawable.pivotX = 0.5f
        knifeDrawable.isPivotYRelative = true
        knifeDrawable.pivotY = 0.95f

        forkDrawable.level = (getHours(System.currentTimeMillis()) / 12 * 10000).toInt()

        val minutes = calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND) / 100f
        knifeDrawable.level = (minutes / 60 * 10000).toInt()

        val seconds = calendar.get(Calendar.SECOND).toFloat() / 60 * Math.PI * 2
        canvas.drawLine(timerCenterX, timerCenterY, timerCenterX + cos(seconds).toFloat() * timerSize * 0.4f, timerCenterY + sin(seconds).toFloat() * timerSize * 0.4f, smallDashPaint)

        forkDrawable.draw(canvas)
        knifeDrawable.draw(canvas)
    }

    private fun updateTimerText() {
        val action = updateTimerText ?: return
        var milliseconds = timerTargetEndDate - System.currentTimeMillis()
        val hours = milliseconds / (1000 * 60 * 60)
        milliseconds -= hours * 1000 * 60 * 60
        val minutes = milliseconds / (1000 * 60)
        milliseconds -= minutes * 1000 * 60
        val seconds = milliseconds / 1000
        action.invoke("${String.format("%02d",abs(hours))}:${String.format("%02d",abs(minutes))}:${String.format("%02d",abs(seconds))}", timerTargetEndDate < System.currentTimeMillis())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 500
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> widthSize
            MeasureSpec.UNSPECIFIED -> desiredWidth
            else -> 0
        }
        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> width
            MeasureSpec.UNSPECIFIED -> width
            else -> 0
        }

        timerSize = min(width, height).toFloat()
        horizontalPadding = (width - timerSize) / 2
        verticalPadding = (height - timerSize) / 2
        timerDashWidth = timerSize * 0.01f
        dashPaint.strokeWidth = timerDashWidth
        smallDashPaint.strokeWidth = timerDashWidth / 2f
        dashMarkerPaint.strokeWidth = timerDashWidth * 3
        textPaint.textSize = timerSize * 0.1f
        timerTextPaint.textSize = timerSize * 0.1f
        timerCenterX = timerSize / 2f + horizontalPadding
        timerCenterY = timerSize / 2f + verticalPadding
        whitePaint.textSize = timerSize * 0.07f
        startButtonCenterX = timerSize / 2f + horizontalPadding
        buttonsCenterY = timerSize / 2f + verticalPadding + timerSize * 0.2f
        buttonsCenterY = timerCenterY + timerSize * buttonsVerticalMargin
        buttonsRadius = timerSize * 0.1f
        hoursButtonCenterY = timerSize / 2f + verticalPadding - timerSize * 0.12f
        hoursButtonRadius = timerSize * 0.15f
        hoursHandSize = timerSize * 0.33f
        setMeasuredDimension(width, height)
    }

    private fun getHours(date: Long): Float {
        calendar.timeInMillis = date
        var hours = calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) / 60f
        if (hours >= 12) {
            hours -= 12
        }
        return hours
    }

    private fun drawDashMarker(angle: Double, canvas: Canvas) {
        val cos = cos(angle).toFloat()
        val sin = sin(angle).toFloat()
        canvas.drawLine(
                timerSize / 2f * cos * 0.7f + timerSize / 2f + horizontalPadding,
                timerSize / 2f * sin * 0.7f + timerSize / 2f + verticalPadding,
                timerSize / 2f * cos + timerSize / 2f + horizontalPadding,
                timerSize / 2f * sin + timerSize / 2f + verticalPadding,
                dashMarkerPaint)
    }
}