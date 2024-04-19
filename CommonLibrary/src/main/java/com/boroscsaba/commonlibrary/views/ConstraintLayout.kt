package com.boroscsaba.commonlibrary.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.ThemeManager
import com.boroscsaba.commonlibrary.Utils


class ConstraintLayout: ConstraintLayout {

    constructor(context: Context): super(context) { init(null) }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) { init(attrs) }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) { init(attrs) }

    private var phase = 0f
    private val path = Path()
    private val intervals = floatArrayOf(15f, 15f)
    private val borderPaint = Paint()
    private val shadowPaint = Paint()
    private var borderType = NO_BORDER
    private val backgroundPaint = Paint()
    private var gradientStartColor: Int? = null
    private var gradientEndColor: Int? = null
    private var shadowSize= 0f

    private fun init(attrs: AttributeSet?) {
        var styleName: String? = null
        if (attrs != null) {
            val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.ConstraintLayout)
            borderType = styledAttributes.getInt(R.styleable.ConstraintLayout_borderType, NO_BORDER)
            styleName = styledAttributes.getString(R.styleable.ConstraintLayout_styleName)
            styledAttributes.recycle()
        }
        setupStyle(styleName)
    }

    fun setStyleName(styleName: String?) {
        setupStyle(styleName)
        postInvalidate()
    }

    private fun setupStyle(styleName: String?) {
        backgroundPaint.shader = null
        gradientStartColor = null
        gradientEndColor = null

        val themeManager = (context.applicationContext as ApplicationBase).themeManager
        if (themeManager.hasGradient(ThemeManager.BACKGROUND_GRADIENT, styleName)) {
            gradientStartColor = themeManager.getGradientStartColor(ThemeManager.BACKGROUND_GRADIENT, styleName)
            gradientEndColor = themeManager.getGradientEndColor(ThemeManager.BACKGROUND_GRADIENT, styleName)
        }
        else if (themeManager.hasColor(ThemeManager.BACKGROUND_COLOR)) {
            backgroundPaint.color = themeManager.getColor(ThemeManager.BACKGROUND_COLOR, styleName)
        }
        val borderColor = themeManager.getColor(ThemeManager.BORDER_COLOR, styleName)
        val shadowColor = themeManager.getColor(ThemeManager.SHADOW_COLOR, styleName)

        borderPaint.color = borderColor
        borderPaint.strokeWidth = Utils.convertDpToPixel(0.5f, context)
        borderPaint.style = Paint.Style.STROKE
        borderPaint.isAntiAlias = true
        shadowPaint.color = shadowColor

        backgroundPaint.style = Paint.Style.FILL
        if (borderType and SHADOW > 0) {
            shadowSize = Utils.convertDpToPixel(6f, context)
            backgroundPaint.setShadowLayer(shadowSize, 0f, 0f, shadowColor)
            setLayerType(LAYER_TYPE_SOFTWARE, backgroundPaint)
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (gradientStartColor != null) {
            backgroundPaint.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), gradientStartColor!!, gradientEndColor!!, Shader.TileMode.CLAMP)
        }

        when {
            borderType and ROUNDED > 0 -> {
                val halfStroke = borderPaint.strokeWidth / 2
                val radius = Utils.convertDpToPixel(20f, context)
                canvas.drawRoundRect(halfStroke + shadowSize, halfStroke + shadowSize, width - halfStroke - shadowSize, height - halfStroke - shadowSize, radius, radius, backgroundPaint)
                canvas.drawRoundRect(halfStroke + shadowSize, halfStroke + shadowSize, width - halfStroke - shadowSize, height - halfStroke - shadowSize, radius, radius, borderPaint)
            }
            borderType and ROUNDED_TOP > 0 -> {
                val halfStroke = borderPaint.strokeWidth / 2
                val radius = Utils.convertDpToPixel(20f, context)
                canvas.drawRoundRect(halfStroke, halfStroke, width - halfStroke, height - halfStroke, radius, radius, backgroundPaint)
                canvas.drawRect(0f, height - radius, width.toFloat(), height.toFloat(), backgroundPaint)
            }
            else -> canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
        }

        if (borderType and DASHED > 0) {
            path.rewind()
            val margins = (layoutParams as (MarginLayoutParams))
            val halfStroke = borderPaint.strokeWidth / 2
            path.addRect(
                    x - margins.leftMargin + halfStroke,
                    halfStroke,
                    x + measuredWidth - margins.leftMargin - halfStroke,
                    measuredHeight - margins.topMargin - halfStroke,
                    Path.Direction.CW)
            canvas.drawPath(path, borderPaint)
            phase += 1
            @SuppressLint("DrawAllocation")
            borderPaint.pathEffect = DashPathEffect(intervals, phase)
            invalidate()
        }
    }

    companion object {
        const val NO_BORDER = 0
        const val DASHED = 1
        const val SOLID = 2
        const val ROUNDED = 4
        const val SHADOW = 8
        const val ROUNDED_TOP = 16
    }

}