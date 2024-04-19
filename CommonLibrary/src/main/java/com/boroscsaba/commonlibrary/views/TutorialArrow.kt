package com.boroscsaba.commonlibrary.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.ImageView
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.ThemeManager
import com.boroscsaba.commonlibrary.Utils

class TutorialArrow: ImageView {

    constructor(context: Context): super(context) { init() }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) { init() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) { init() }

    private fun init() {
        Utils.setImageViewSource(R.drawable.tutorial_arrow, this, context)
        imageTintList = ColorStateList.valueOf((context.applicationContext as ApplicationBase).themeManager.getColor(ThemeManager.TUTORIAL_ARROW_COLOR))
    }
}