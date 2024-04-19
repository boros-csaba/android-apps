package com.boroscsaba.bodymeasurementtracker

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.boroscsaba.commonlibrary.viewelements.Popup
import kotlinx.android.synthetic.main.unit_selector_layout.view.*

class ViewController {
    companion object {

        fun showUnitSelector(initialUnit: String, presetType: MeasurementPresetTypeEnum, context: Context, action: (String) -> Unit) {
            val popup = Popup(R.layout.unit_selector_layout, context)
            popup.title = context.getString(R.string.measurement_unit)
            popup.okAction = { view ->
                action.invoke(view.customUnit.text.toString())
                popup.popup?.dismiss()
            }
            popup.show { view ->
                val units = when (presetType) {
                    MeasurementPresetTypeEnum.WEIGHT -> arrayOf("lbs", "kg", "st")
                    MeasurementPresetTypeEnum.HEIGHT -> arrayOf("in", "cm")
                    else -> {
                        view.customUnit.isEnabled = true
                        arrayOf("in", "cm", "lbs", "kg", "st", "%", "")
                    }
                }
                val adapter = ArrayAdapter<String>(context, com.boroscsaba.commonlibrary.R.layout.simple_spinner_item, units)
                view.unitSpinner.adapter = adapter
                view.unitSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, itemView: View?, position: Int, id: Long) {
                        val selected = itemView?.findViewById<TextView>(R.id.text)?.text?.toString()
                        view.customUnit.setText(if (selected.isNullOrEmpty()) initialUnit else selected)
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
                if (adapter.getPosition(initialUnit) >= 0) {
                    view.unitSpinner.setSelection(adapter.getPosition(initialUnit))
                }
                else {
                    view.unitSpinner.setSelection(adapter.getPosition(""))
                    view.customUnit.setText(initialUnit)
                }
            }
        }
    }
}