package com.boroscsaba.commonlibrary.splashscreen

import android.content.Context
import android.graphics.*
import android.util.TypedValue
import android.view.View
import com.boroscsaba.commonlibrary.R


class SplashScreenView(context: Context) : View(context) {

    private var phaseOne = true
    private var startAngle = 0f
    private var sweepAngle = 0f
    private val angleSpeed = 19

    private val backgroundPaint = Paint()
    private val loadingPaint = Paint()
    private val iconSize = 0.25f
    private val iconRect = Rect()


    init {
        loadingPaint.color = Color.parseColor("#ffffff")
        loadingPaint.alpha = 100

        val value = TypedValue()
        context.theme.resolveAttribute(R.attr.colorPrimary, value, true)
        backgroundPaint.color = value.data
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
        canvas.drawArc(width / 2 - width.toFloat(), height / 2 - height.toFloat(), width / 2 + width.toFloat(), height / 2 + height.toFloat(),
                startAngle - 90f, sweepAngle, true, loadingPaint)
        if (phaseOne) {
            sweepAngle += angleSpeed
            if (sweepAngle > 360) {
                phaseOne = false
                sweepAngle = 360f
            }
        }
        else {
            sweepAngle -= angleSpeed
            startAngle += angleSpeed
            if (sweepAngle <= 0) {
                phaseOne = true
                startAngle = 0f
            }
        }

        if (sweepAngle > 360) {
            sweepAngle = 0f
        }

        val iconSize = width * iconSize
        iconRect.left = ((width - iconSize) / 2).toInt()
        iconRect.right = ((width + iconSize) / 2).toInt()
        iconRect.top = ((height - iconSize) / 2).toInt()
        iconRect.bottom = ((height + iconSize) / 2).toInt()

        invalidate()
    }
}