package com.boroscsaba.bodymeasurementtracker.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boroscsaba.bodymeasurementtracker.MeasurementPresetTypeEnum
import com.boroscsaba.bodymeasurementtracker.R
import com.boroscsaba.bodymeasurementtracker.model.Parameter
import com.boroscsaba.bodymeasurementtracker.viewmodel.MainViewModel
import com.boroscsaba.commonlibrary.helpers.KeyboardHelper
import kotlinx.android.synthetic.main.dashboard_item_bmi_view.*


class DashboardItemBmiView : Fragment() {

    private var viewModel: MainViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dashboard_item_bmi_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        }
        val viewModel = this.viewModel ?: return
        viewModel.parameters.observe(this, Observer { parameters ->
            initValues(parameters)
        })
        val parameters = viewModel.parameters.value
        if (parameters != null) {
            initValues(parameters)
        }

        quickEditButton.setOnClickListener {
            setEditMode()
        }
        quickEditSaveButton.setOnClickListener {
            saveChanges()
            (parentFragment as MainPageView).setDisplayMode()
        }
        discardChangesButton.setOnClickListener { (parentFragment as MainPageView).setDisplayMode() }
    }

    private fun initValues(parameters: ArrayList<Parameter>) {
        val app = activity?.application
        if (app != null) {
            val heightParameter = parameters.firstOrNull { p -> p.presetType == MeasurementPresetTypeEnum.HEIGHT }
            val heightMeasurement = heightParameter?.measurements?.firstOrNull()
            val height = heightMeasurement?.value ?: 0.0
            val heightUnitString = heightParameter?.unit ?: viewModel?.defaultUnit?.value ?: "in"

            val weightParameter = parameters.firstOrNull { p -> p.presetType == MeasurementPresetTypeEnum.WEIGHT }
            val weightMeasurement = weightParameter?.measurements?.firstOrNull()
            val weight = weightMeasurement?.value ?: 0.0
            val weightUnitString = weightParameter?.unit ?: if (viewModel?.defaultUnit?.value == "cm") "kg" else "lbs"

            bmiChart.setHeightAndWeight(height, heightUnitString, weight, weightUnitString)
            heightUnit?.text = heightUnitString
            heightEdit?.setText(String.format("%.1f", height))
            weightUnit?.text = weightUnitString
            weightEdit?.setText(String.format("%.1f", weight))
        }
    }

    private fun setEditMode() {
        (parentFragment as MainPageView).setEditMode(tag!!)
        bmiChart?.visibility = View.GONE
        bmiChartEdit?.visibility = View.VISIBLE

        quickEditButton.visibility = View.GONE
        discardChangesButton.visibility = View.VISIBLE
        quickEditSaveButton.visibility = View.VISIBLE
    }

    fun setDisplayMode(): Boolean {
        val wasEditMode = bmiChart?.visibility == View.GONE
        if (!wasEditMode) return false

        bmiChart?.visibility = View.VISIBLE
        bmiChartEdit?.visibility = View.GONE

        quickEditButton.visibility = View.VISIBLE
        quickEditSaveButton.visibility = View.GONE
        discardChangesButton.visibility = View.GONE

        val parentActivity = activity
        if (parentActivity != null) {
            KeyboardHelper(parentActivity).hideKeyboard()
        }

        return wasEditMode
    }

    private fun saveChanges() {
        val newHeight = heightEdit?.text.toString()
        val newWeight = weightEdit?.text.toString()
        viewModel?.saveBmiChanges(newHeight, newWeight)
    }
}