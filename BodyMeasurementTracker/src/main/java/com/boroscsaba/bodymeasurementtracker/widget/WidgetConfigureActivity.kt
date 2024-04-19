package com.boroscsaba.bodymeasurementtracker.widget

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boroscsaba.bodymeasurementtracker.MeasurementPresetTypeEnum
import com.boroscsaba.bodymeasurementtracker.R
import com.boroscsaba.bodymeasurementtracker.logic.ParameterLogic
import com.boroscsaba.bodymeasurementtracker.model.Parameter
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.commonlibrary.widget.WidgetConfigureActivityBase

class WidgetConfigureActivity: WidgetConfigureActivityBase<Parameter>() {

    override val widgetClass: Class<*> = Widget::class.java
    override val rowItemLayoutId: Int = R.layout.widget_configure_item_layout

    override fun getData() {
        ParameterLogic(application).getParameters(values)
    }

    override fun onBindItemViewHolder(item: Parameter, holder: RecyclerView.ViewHolder) {
        holder.itemView.findViewById<TextView>(R.id.name)?.text = item.name
        holder.itemView.findViewById<TextView>(R.id.letter)?.backgroundTintList = ColorStateList.valueOf(Color.parseColor(item.color))

        if (item.presetType == MeasurementPresetTypeEnum.CUSTOM || item.presetType == MeasurementPresetTypeEnum.BMI) {
            holder.itemView.findViewById<TextView>(R.id.letter)?.text = if (item.name.isNotEmpty()) item.name[0].toString() else ""
        }
        else {
            val icon = holder.itemView.findViewById<ImageView>(R.id.icon)
            Utils.setImageViewSource(MeasurementPresetTypeEnum.getIcon(item.presetType), icon, this)
            val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MeasurementPresetTypeEnum.getIconPadding(item.presetType).toFloat(), resources.displayMetrics).toInt()
            icon?.setPadding(padding, padding, padding, padding)
        }
    }
}