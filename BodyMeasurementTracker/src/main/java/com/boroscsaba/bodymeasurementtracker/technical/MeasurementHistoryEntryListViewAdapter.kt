package com.boroscsaba.bodymeasurementtracker.technical

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.boroscsaba.bodymeasurementtracker.R
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.boroscsaba.bodymeasurementtracker.MeasurementPresetTypeEnum
import com.boroscsaba.bodymeasurementtracker.model.MeasurementLogEntry
import com.boroscsaba.commonlibrary.Utils


/**
* Created by boros on 2/11/2018.
*/
class MeasurementHistoryEntryListViewAdapter(context: Context, private var items: List<MeasurementLogEntry>) : ArrayAdapter<MeasurementLogEntry>(context, R.layout.measurement_history_sub_item_layout, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.measurement_history_sub_item_layout, parent, false) ?: return convertView
        }
        val measurement = items[position]
        val parameter = measurement.parameter ?: return view

        view.findViewById<TextView>(R.id.name)?.text = parameter.name
        view.findViewById<TextView>(R.id.letter)?.backgroundTintList = ColorStateList.valueOf(Color.parseColor(parameter.color))
        view.findViewById<TextView>(R.id.value)?.text = String.format("%.1f %s", measurement.value, parameter.unit)
        if (measurement.parameter?.presetType == MeasurementPresetTypeEnum.CUSTOM) {
            view.findViewById<TextView>(R.id.letter)?.text = if (parameter.name.isNotEmpty()) parameter.name[0].toString() else ""
        }
        else {
            val icon = view.findViewById<ImageView>(R.id.icon)
            Utils.setImageViewSource(MeasurementPresetTypeEnum.getIcon(parameter.presetType), icon, context)
            val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MeasurementPresetTypeEnum.getIconPadding(parameter.presetType).toFloat(), context.resources.displayMetrics).toInt()
            icon?.setPadding(padding, padding, padding, padding)
        }
        return view
    }
}