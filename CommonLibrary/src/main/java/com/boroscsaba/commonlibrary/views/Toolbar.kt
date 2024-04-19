package com.boroscsaba.commonlibrary.views

import android.content.Context
import android.util.AttributeSet
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.ThemeManager

class Toolbar: androidx.appcompat.widget.Toolbar {

    constructor(context: Context): super(context) { init(null) }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) { init(attrs) }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) { init(attrs) }


    private fun init(attrs: AttributeSet?) {
        val themeManager = (context.applicationContext as ApplicationBase).themeManager
        if (themeManager.hasColor(ThemeManager.TOOLBAR_BACKGROUND_COLOR)) {
            setBackgroundColor(themeManager.getColor(ThemeManager.TOOLBAR_BACKGROUND_COLOR))
        }
        else {
            setBackgroundColor(themeManager.getColor(ThemeManager.FOREGROUND_COLOR))
        }
    }
}