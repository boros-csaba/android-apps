package com.boroscsaba.commonlibrary.views

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.ThemeManager

class TextView: TextView {

    constructor(context: Context): super(context) { init(null) }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) { init(attrs) }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) { init(attrs) }

    private fun init(attrs: AttributeSet?) {
        var styleName: String? = null

        if (attrs != null) {
            val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.TextView)
            styleName = styledAttributes.getString(R.styleable.TextView_styleName)
            styledAttributes.recycle()
        }
        setupTheme(styleName)
    }

    private fun setupTheme(styleName: String?) {
        val themeManager = (context.applicationContext as ApplicationBase).themeManager
        setTextColor(themeManager.getColor(ThemeManager.PRIMARY_TEXT_COLOR, styleName))
    }

    fun setStyle(styleName: String) {
        setupTheme(styleName)
    }

    fun setTextColorName(colorName: String) {
        val themeManager = (context.applicationContext as ApplicationBase).themeManager
        setTextColor(themeManager.getColor(colorName))
    }
}