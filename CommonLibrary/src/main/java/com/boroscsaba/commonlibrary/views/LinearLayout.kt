package com.boroscsaba.commonlibrary.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.ThemeManager

class LinearLayout: LinearLayout {

    constructor(context: Context): super(context) { init(null) }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) { init(attrs) }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) { init(attrs) }

    private fun init(attrs: AttributeSet?) {
        val themeManager = (context.applicationContext as ApplicationBase).themeManager
        var styleName: String? = null

        if (attrs != null) {
            val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.LinearLayout)
            styleName = styledAttributes.getString(R.styleable.LinearLayout_styleName)
            styledAttributes.recycle()
        }
        setBackgroundColor(themeManager.getColor(ThemeManager.BACKGROUND_COLOR, styleName))
    }
}