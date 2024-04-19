package com.boroscsaba.commonlibrary.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.MutableLiveData
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.ThemeManager
import com.boroscsaba.commonlibrary.Utils

class MultiStateToggle: LinearLayout {

    constructor(context: Context): super(context) { init(null) }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) { init(attrs) }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) { init(attrs) }

    private var styleName: String? = null
    private var liveData: MutableLiveData<Int>? = null

    private fun init(attrs: AttributeSet?) {
        val themeManager = (context.applicationContext as ApplicationBase).themeManager
        var option1Text: String? = null
        var option2Text: String? = null
        var option3Text: String? = null

        if (attrs != null) {
            val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.MultiStateToggle)
            styleName = styledAttributes.getString(R.styleable.MultiStateToggle_styleName)
            option1Text = styledAttributes.getString(R.styleable.MultiStateToggle_option1Text)
            option2Text = styledAttributes.getString(R.styleable.MultiStateToggle_option2Text)
            option3Text = styledAttributes.getString(R.styleable.MultiStateToggle_option3Text)
            styledAttributes.recycle()
        }
        setBackgroundColor(themeManager.getColor(ThemeManager.INACTIVE_BACKGROUND_BACKGROUND_COLOR, styleName))

        val verticalPadding = Utils.convertDpToPixel(8f, context).toInt()
        if (option1Text != null) {
            val textView = TextView(context)
            textView.id = R.id.option1
            val layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT)
            layoutParams.weight = 1f
            textView.layoutParams = layoutParams
            textView.text = option1Text
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            textView.gravity = Gravity.CENTER
            textView.setPadding(0, verticalPadding, 0, verticalPadding)
            textView.setOnClickListener { setActive(1) }
            addView(textView)
        }
        if (option2Text != null) {
            val textView = TextView(context)
            textView.id = R.id.option2
            val layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT)
            layoutParams.weight = 1f
            textView.layoutParams = layoutParams
            textView.text = option2Text
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            textView.gravity = Gravity.CENTER
            textView.setPadding(0, verticalPadding, 0, verticalPadding)
            textView.setOnClickListener { setActive(2) }
            addView(textView)
        }
        if (option3Text != null) {
            val textView = TextView(context)
            textView.id = R.id.option3
            val layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT)
            layoutParams.weight = 1f
            textView.layoutParams = layoutParams
            textView.text = option3Text
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            textView.gravity = Gravity.CENTER
            textView.setPadding(0, verticalPadding, 0, verticalPadding)
            textView.setOnClickListener { setActive(3) }
            addView(textView)
        }
        setActive(1)
    }

    fun setActive(option: Int) {
        liveData?.value = option
        val themeManager = (context.applicationContext as ApplicationBase).themeManager
        if (option == 1) {
            findViewById<TextView>(R.id.option1)?.setBackgroundColor(themeManager.getColor(ThemeManager.ACTIVE_BACKGROUND_BACKGROUND_COLOR, styleName))
        }
        else {
            findViewById<TextView>(R.id.option1)?.setBackgroundColor(themeManager.getColor(ThemeManager.INACTIVE_BACKGROUND_BACKGROUND_COLOR, styleName))
        }
        if (option == 2) {
            findViewById<TextView>(R.id.option2)?.setBackgroundColor(themeManager.getColor(ThemeManager.ACTIVE_BACKGROUND_BACKGROUND_COLOR, styleName))
        }
        else {
            findViewById<TextView>(R.id.option2)?.setBackgroundColor(themeManager.getColor(ThemeManager.INACTIVE_BACKGROUND_BACKGROUND_COLOR, styleName))
        }
        if (option == 3) {
            findViewById<TextView>(R.id.option3)?.setBackgroundColor(themeManager.getColor(ThemeManager.ACTIVE_BACKGROUND_BACKGROUND_COLOR, styleName))
        }
        else {
            findViewById<TextView>(R.id.option3)?.setBackgroundColor(themeManager.getColor(ThemeManager.INACTIVE_BACKGROUND_BACKGROUND_COLOR, styleName))
        }
    }

    fun setChangeListener(liveData: MutableLiveData<Int>) {
        this.liveData = liveData
    }
}