package com.boroscsaba.commonlibrary.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.ThemeManager

class TabLayout: com.google.android.material.tabs.TabLayout {

    private val backgroundPaint = Paint()
    private var gradientStartColor: Int? = null
    private var gradientEndColor: Int? = null

    constructor(context: Context): super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) {
        init()
    }

    /*fun withStyleName() {
        val themeManager = (context.applicationContext as ApplicationBase).themeManager
        if (themeManager.hasGradient(styleName)) {
            gradientStartColor = themeManager.getGradientStartColor(styleName)
            gradientEndColor = themeManager.getGradientEndColor(styleName)
        }
        else {
            backgroundPaint.color = themeManager.getColor(styleName, ThemeManager.FOREGROUND_COLOR)
        }
    }*/

    /*fun getStyleName(): String? {
        return styleName
    }*/

    private fun init() {
        backgroundPaint.isAntiAlias = true
        val themeManager = (context.applicationContext as ApplicationBase).themeManager
        when {
            themeManager.hasGradient(ThemeManager.TAB_LAYOUT_BACKGROUND_GRADIENT) -> {
                gradientStartColor = themeManager.getGradientStartColor(ThemeManager.TAB_LAYOUT_BACKGROUND_GRADIENT)
                gradientEndColor = themeManager.getGradientEndColor(ThemeManager.TAB_LAYOUT_BACKGROUND_GRADIENT)
            }
            themeManager.hasColor(ThemeManager.TAB_LAYOUT_BACKGROUND_COLOR) -> backgroundPaint.color = themeManager.getColor(ThemeManager.TAB_LAYOUT_BACKGROUND_COLOR)
            else -> backgroundPaint.color = themeManager.getColor(ThemeManager.FOREGROUND_COLOR)
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (gradientStartColor != null) {
            backgroundPaint.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), gradientStartColor!!, gradientEndColor!!, Shader.TileMode.CLAMP)
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
    }
}