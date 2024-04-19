package com.boroscsaba.bodymeasurementtracker.view

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import com.boroscsaba.bodymeasurementtracker.MeasurementPresetTypeEnum
import com.boroscsaba.bodymeasurementtracker.R
import com.boroscsaba.bodymeasurementtracker.model.Parameter
import com.boroscsaba.bodymeasurementtracker.viewmodel.MainViewModel
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.commonlibrary.activities.AdsDisplayOptions
import com.boroscsaba.commonlibrary.activities.FragmentBase
import com.boroscsaba.commonlibrary.adapters.SimpleArrayAdapter
import com.google.android.gms.ads.AdSize
import kotlinx.android.synthetic.main.fragment_chart.*

class ChartView : FragmentBase(R.layout.fragment_chart) {

    init {
        options.adsOptions = AdsDisplayOptions(AdSize.SMART_BANNER, R.id.AdContainerChart, "ca-app-pub-7535188687645300/6477778676")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        }
        parameterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val parameter = parent?.getItemAtPosition(position)
                val parameterId = if (parameter != null) (parameter as Parameter).id else 0
                viewModel?.updateDataSource(parameterId)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                parameterSpinner.setSelection(0)
            }
        }

        viewModel?.parameters?.observe(this, Observer { values ->
            if (values != null) {
                val parameters = ArrayList(values.filter { p -> p.presetType.value < 100 && p.dashboardEnabled })
                if (parameters.isNotEmpty()) {
                    parameterSpinner.adapter = SimpleArrayAdapter(context!!, R.layout.spinner_parameter_item_layout, parameters, { view, parameter ->
                        val parameterNameTextView = view.findViewById<TextView>(R.id.parameterName)
                        parameterNameTextView.text = parameter.name
                        val icon = view.findViewById<ImageView>(R.id.icon)
                        val iconText = view.findViewById<TextView>(R.id.letter)
                        if (parameter.presetType != MeasurementPresetTypeEnum.CUSTOM) {
                            iconText.text = ""
                            Utils.setImageViewSource(MeasurementPresetTypeEnum.getIcon(parameter.presetType), icon, context!!)
                        }
                        else {
                            iconText.text = if (parameter.name.isNotEmpty()) parameter.name[0].toString() else ""
                            icon.setImageBitmap(null)
                        }
                        val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MeasurementPresetTypeEnum.getIconPadding(parameter.presetType).toFloat(), activity?.resources?.displayMetrics).toInt()
                        icon.setPadding(padding, padding, padding, padding)
                        iconText.backgroundTintList = ColorStateList.valueOf(Color.parseColor(parameter.color))
                    }, { parameter -> parameter.id.toString() })
                    parameterSpinner?.setSelection(0)
                }
            }
        })
        viewModel?.chartDataSource?.observe(this, Observer { d ->
            if (d != null) lineChart.setDataSource(ArrayList(d))
        })
        viewModel?.chartColor?.observe(this, Observer { color ->
            lineChart.setLineColor(color)
        })
    }
}
