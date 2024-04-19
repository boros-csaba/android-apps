package com.boroscsaba.commonlibrary.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.ImageView
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.ThemeManager

class ImageView: ImageView {

    constructor(context: Context): super(context) { init(null) }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) { init(attrs) }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) { init(attrs) }

    private fun init(attrs: AttributeSet?) {
        val themeManager = (context.applicationContext as ApplicationBase).themeManager
        var styleName: String? = null

        if (attrs != null) {
            val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.ImageView)
            styleName = styledAttributes.getString(R.styleable.ImageView_styleName)
            styledAttributes.recycle()
        }
        if (themeManager.hasColor(ThemeManager.IMAGE_TINT, styleName)) {
            imageTintList = ColorStateList.valueOf(themeManager.getColor(ThemeManager.IMAGE_TINT, styleName))
        }
        setBackgroundColor(themeManager.getColor(ThemeManager.BACKGROUND_COLOR, styleName))
    }

}