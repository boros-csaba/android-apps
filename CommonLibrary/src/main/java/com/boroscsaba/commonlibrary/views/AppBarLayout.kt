package com.boroscsaba.commonlibrary.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.graphics.RectF



class AppBarLayout: com.google.android.material.appbar.AppBarLayout {

    private val backgroundCurveRadius = 0.3f
    private val backgroundPaint = Paint()
    private val backgroundPath = Path()

    constructor(context: Context): super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        backgroundPaint.isAntiAlias = true
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val arcOffset = width.toFloat() * backgroundCurveRadius

        backgroundPath.moveTo(0f, 0f)
        backgroundPath.lineTo(width.toFloat(), 0f)
        backgroundPath.lineTo(width.toFloat(), height.toFloat())
        backgroundPath.arcTo(RectF(-arcOffset , 0f, width.toFloat() + arcOffset, height.toFloat()), 0f, 180f)
        backgroundPath.lineTo(0f, 0f)

        backgroundPaint.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), Color.parseColor("#D32F2F"), Color.parseColor("#F44336"), Shader.TileMode.CLAMP)
        canvas?.drawPath(backgroundPath, backgroundPaint)
    }
}