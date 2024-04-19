package com.boroscsaba.bodymeasurementtracker.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import com.boroscsaba.bodymeasurementtracker.MeasurementPresetTypeEnum
import com.boroscsaba.bodymeasurementtracker.R
import com.boroscsaba.bodymeasurementtracker.model.Parameter
import com.boroscsaba.bodymeasurementtracker.viewmodel.MainViewModel
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.commonlibrary.adapters.SimpleRecyclerViewAdapter
import kotlinx.android.synthetic.main.edit_values_popup.view.*
import kotlinx.android.synthetic.main.edit_values_popup_item.view.*
import kotlin.math.abs

class MeasurementsEditorPopup {

    fun show(viewModel: MainViewModel?, context: Activity) {
        val parameters = viewModel?.parameters?.value?.filter { p -> p.presetType.value < 100 }
        if (parameters != null) {
            @SuppressLint("InflateParams")
            val view = LayoutInflater.from(context).inflate(R.layout.edit_values_popup, null)
            view.editorPopupList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            val holders = ArrayList<Pair<androidx.recyclerview.widget.RecyclerView.ViewHolder, Parameter>>()
            view.editorPopupList.adapter = SimpleRecyclerViewAdapter(ArrayList(parameters), R.layout.edit_values_popup_item, { holder, parameter ->
                holder.setIsRecyclable(false)
                holders.add(Pair(holder, parameter))
                val measurement = parameter.measurements.firstOrNull()
                holder.itemView.name.text = parameter.name
                holder.itemView.letter.backgroundTintList = ColorStateList.valueOf(Color.parseColor(parameter.color))
                holder.itemView.value.text = String.format("%.1f", measurement?.value ?: 0.0)
                holder.itemView.unit.text = parameter.unit
                if (parameter.presetType == MeasurementPresetTypeEnum.CUSTOM) {
                    holder.itemView.letter.text = if (parameter.name.isNotEmpty()) parameter.name[0].toString() else ""
                }
                else {
                    Utils.setImageViewSource(MeasurementPresetTypeEnum.getIcon(parameter.presetType), holder.itemView.icon, context)
                    val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MeasurementPresetTypeEnum.getIconPadding(parameter.presetType).toFloat(), context.resources.displayMetrics).toInt()
                    holder.itemView.icon.setPadding(padding, padding, padding, padding)
                }
                holder.itemView.stepperDecrease.setOnClickListener { changeValue(holder, measurement?.value ?: 0.0, false) }
                holder.itemView.stepperIncrease.setOnClickListener { changeValue(holder, measurement?.value ?: 0.0, true) }
            })
            AlertDialog.Builder(context).setTitle("")
                    .setPositiveButton(context.getString(com.boroscsaba.commonlibrary.R.string.save)) { _, _ ->
                        holders.forEach { holder ->
                            val value = Utils.toDoubleOrNull(holder.first.itemView.value.text.toString())
                            val originalValue = holder.second.measurements.firstOrNull()?.value ?: 0.0
                            if (value != null && abs(value - originalValue) > 0.01) {
                                viewModel.saveValueChange(holder.second.id, holder.first.itemView.value.text.toString())
                            }
                        }
                    }
                    .setNegativeButton(context.getString(com.boroscsaba.commonlibrary.R.string.cancel)) { _, _ -> }
                    .setView(view)
                    .setCancelable(false)
                    .show()
        }
    }

    private fun changeValue(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, originalValue: Double, increase: Boolean) {
        var currentValue = Utils.toDoubleOrNull(holder.itemView.value.text.toString()) ?: 0.0
        if (increase) currentValue += 0.1
        else currentValue -= 0.1
        if (currentValue < 0) currentValue = 0.0
        holder.itemView.value.text = String.format("%.1f", currentValue)
        if (abs(currentValue - originalValue) < 0.01) {
            undoValueChangeHighlight(holder)
        }
        else {
            highlightValueChange(holder)
        }
    }

    private fun highlightValueChange(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        holder.itemView.value.textSize = 14f
        holder.itemView.unit.textSize = 14f
        holder.itemView.value.setTextColor(Color.parseColor("#333333"))
        holder.itemView.unit.setTextColor(Color.parseColor("#333333"))
        holder.itemView.value.setTypeface(holder.itemView.value.typeface, Typeface.BOLD)
        holder.itemView.unit.setTypeface(holder.itemView.unit.typeface, Typeface.BOLD)
    }

    private fun undoValueChangeHighlight(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        holder.itemView.value.textSize = 12f
        holder.itemView.unit.textSize = 12f
        holder.itemView.value.setTextColor(Color.parseColor("#898989"))
        holder.itemView.unit.setTextColor(Color.parseColor("#898989"))
        holder.itemView.value.setTypeface(holder.itemView.value.typeface, Typeface.NORMAL)
        holder.itemView.unit.setTypeface(holder.itemView.unit.typeface, Typeface.NORMAL)
    }
}