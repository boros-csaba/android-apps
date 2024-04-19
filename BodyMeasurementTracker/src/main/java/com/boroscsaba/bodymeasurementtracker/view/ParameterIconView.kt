package com.boroscsaba.bodymeasurementtracker.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.RelativeLayout
import com.boroscsaba.bodymeasurementtracker.MeasurementPresetTypeEnum
import com.boroscsaba.bodymeasurementtracker.R
import com.boroscsaba.bodymeasurementtracker.model.Parameter
import com.boroscsaba.commonlibrary.Utils
import kotlinx.android.synthetic.main.parameter_icon_layout.view.*

class ParameterIconView: RelativeLayout {

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
        inflate(context, R.layout.parameter_icon_layout,this)
    }

    fun setup(parameter: Parameter) {
        if (parameter.presetType == MeasurementPresetTypeEnum.CUSTOM) {
            setInitials(parameter.name)
        }
        else if (parameter.presetType != MeasurementPresetTypeEnum.BMI) {
            Utils.setImageViewSource(MeasurementPresetTypeEnum.getIcon(parameter.presetType), icon, context)
            val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MeasurementPresetTypeEnum.getIconPadding(parameter.presetType).toFloat() / 2f, context.resources.displayMetrics).toInt()
            icon.setPadding(padding, padding, padding, padding)
        }
        setBackground(parameter.color)
    }

    fun setBackground(color: String) {
        letter.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
    }

    fun setInitials(text: String) {
        if (text.isNotEmpty()) {
            letter.text = text.toUpperCase()[0].toString()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        letter.textSize = MeasureSpec.getSize(widthMeasureSpec) / 5f
    }
}