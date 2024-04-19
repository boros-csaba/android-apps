package com.boroscsaba.englishirregularverbsmemorizer.viewItems

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.TextView
import com.boroscsaba.englishirregularverbsmemorizer.R
import kotlin.math.max

class DailyProgressView(context: Context, attributeSet: AttributeSet): TextView(context, attributeSet) {

    private val backgroundFillPaint = Paint()
    private val completedFillPaint = Paint()
    private var dailyGoal = 1
    private var progress = 0

    init {
        backgroundFillPaint.color = Color.parseColor("#fea745")
        completedFillPaint.color = Color.parseColor("#50d393")
        setText()
    }

    fun setDailyGoal(goal: Int) {
        this.dailyGoal = goal
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        setText()
        postInvalidate()
    }

    private fun setText() {
        val text = if (dailyGoal == progress) {
            context.getText(R.string.daily_goal_met)
        }
        else {
            "${context.getText(R.string.daily_goal)}: $progress/$dailyGoal"
        }
        this.text = text
    }

    override fun onDraw(canvas: Canvas) {
        val startWidth = width.toFloat() * max(progress.toFloat() / dailyGoal, 0f)
        canvas.drawRect(0f, 0f, startWidth, height.toFloat(), completedFillPaint)
        canvas.drawRect(startWidth, 0f, width.toFloat(), height.toFloat(), backgroundFillPaint)
        super.onDraw(canvas)
    }
}