package com.boroscsaba.bodymeasurementtracker.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boroscsaba.bodymeasurementtracker.R
import com.boroscsaba.bodymeasurementtracker.ViewController
import com.boroscsaba.bodymeasurementtracker.model.Parameter
import com.boroscsaba.bodymeasurementtracker.viewmodel.ParameterViewModel
import com.boroscsaba.commonlibrary.helpers.KeyboardHelper
import kotlinx.android.synthetic.main.dashboard_item_current_value_view.*
import kotlin.math.abs

class DashboardItemCurrentValueView : Fragment(){

    private var viewModel: ParameterViewModel? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val parentFragment = parentFragment ?: return
        viewModel = ViewModelProviders.of(parentFragment).get(ParameterViewModel::class.java)
        val viewModel = this.viewModel ?: return
        viewModel.parameter.observe(this, Observer { parameter ->
            if (parameter != null) {
                initValues(parameter)
            }
        })
        val parameter = viewModel.parameter.value
        if (parameter != null) {
            initValues(parameter)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dashboard_item_current_value_view, container, false)
    }

    private fun initValues(parameter: Parameter) {
        parameter.measurements.sortByDescending { measurement -> measurement.logDate }
        if (lastValueUnit.text != parameter.unit) {
            lastValueUnit.text = parameter.unit
        }
        val lastEntry = parameter.measurements.firstOrNull()
        if (lastEntry != null) {
            lastValue.text = String.format("%.1f", lastEntry.value)
            lastValueEdit.hint = String.format("%.1f", lastEntry.value)
            lastValueEdit.setText("")
            val secondLastEntry = parameter.measurements.firstOrNull { d -> d.value != lastEntry.value }
            if (secondLastEntry != null) {
                val change = lastEntry.value - secondLastEntry.value
                changeValue.text = String.format("%.1f %s", abs(change), parameter.unit)
                changePeriod.text = String.format("in ${(lastEntry.logDate - secondLastEntry.logDate) / 1000 / 60 / 60 / 24 + 1} days")
                changeDirection.setImageResource(R.drawable.ic_arrow_downward_white_48dp)
                changeDirection.rotation = if (change < 0) 0f else 180f
            }
        }

        lastValueUnitEdit.keyListener = null
        lastValueUnitEdit.setText(parameter.unit)
        lastValueUnitEdit.setOnClickListener {
            ViewController.showUnitSelector(parameter.unit, parameter.presetType, context!!) { newUnit ->
                parameter.unit = newUnit
                initValues(parameter)
            }
        }
    }

    fun setEditMode() {
        setFieldsVisibility(true)
    }

    fun setDisplayMode() {
        val parentActivity = activity
        if (parentActivity != null) {
            KeyboardHelper(parentActivity).hideKeyboard()
        }
        setFieldsVisibility(false)
    }

    private fun setFieldsVisibility(editMode: Boolean) {
        val editorVisibility = if (editMode) View.VISIBLE else View.GONE
        val displayVisibility = if (!editMode) View.VISIBLE else View.GONE

        lastValue.visibility = displayVisibility
        lastValueUnit.visibility = displayVisibility

        lastValueEdit.visibility = editorVisibility
        lastValueUnitEdit.visibility = editorVisibility
    }

    fun saveChanges() {
        viewModel?.saveValueChange(lastValueEdit.text.toString())
    }
}