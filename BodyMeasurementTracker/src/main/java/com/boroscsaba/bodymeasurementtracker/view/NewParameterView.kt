package com.boroscsaba.bodymeasurementtracker.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.ViewModelProviders
import com.boroscsaba.bodymeasurementtracker.MeasurementPresetTypeEnum
import com.boroscsaba.bodymeasurementtracker.R
import com.boroscsaba.bodymeasurementtracker.ViewController
import com.boroscsaba.bodymeasurementtracker.activities.MainActivity
import com.boroscsaba.bodymeasurementtracker.viewmodel.MainViewModel
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.commonlibrary.activities.FragmentBase
import com.boroscsaba.commonlibrary.helpers.KeyboardHelper
import com.boroscsaba.commonlibrary.viewelements.Popup
import kotlinx.android.synthetic.main.dashboard_item_new_parameter_view.*
import kotlinx.android.synthetic.main.parameter_color_picker_layout.view.*
import kotlinx.android.synthetic.main.parameter_icon_layout.view.*

class NewParameterView: FragmentBase(R.layout.dashboard_item_new_parameter_view) {

    private var viewModel: MainViewModel? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        }

        newParameterUnitEdit.keyListener = null
        newParameterUnitEdit.setText(getString(R.string.inch))
        newParameterUnitEdit.setOnClickListener {
            ViewController.showUnitSelector(getString(R.string.inch), MeasurementPresetTypeEnum.CUSTOM, context!!) { newUnit ->
                newParameterUnitEdit.setText(newUnit)
            }
        }

        newParameterSaveButton.setOnClickListener { saveNewParameter() }
        iconColor = MeasurementPresetTypeEnum.getRandomColor()
        newParameterIcon.setBackground(iconColor)
        newParameterIcon.letter.setOnClickListener {
            val popup = Popup(R.layout.parameter_color_picker_layout, context!!)
            popup.show { view ->
                view.color0.setOnClickListener { selectColor(0); popup.popup?.dismiss() }
                view.color1.setOnClickListener { selectColor(1); popup.popup?.dismiss() }
                view.color2.setOnClickListener { selectColor(2); popup.popup?.dismiss() }
                view.color3.setOnClickListener { selectColor(3); popup.popup?.dismiss() }
                view.color4.setOnClickListener { selectColor(4); popup.popup?.dismiss() }
                view.color5.setOnClickListener { selectColor(5); popup.popup?.dismiss() }
                view.color6.setOnClickListener { selectColor(6); popup.popup?.dismiss() }
                view.color7.setOnClickListener { selectColor(7); popup.popup?.dismiss() }
                view.color8.setOnClickListener { selectColor(8); popup.popup?.dismiss() }
                view.color9.setOnClickListener { selectColor(9); popup.popup?.dismiss() }
            }
        }
        newParameterNameEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { newParameterIcon.setInitials(s.toString()) }
        })
    }

    private var iconColor: String = MeasurementPresetTypeEnum.colors[0]
    private fun saveNewParameter() {
        KeyboardHelper(activity!!).hideKeyboard()

        val name = newParameterNameEdit.text.toString()
        if (name.length < 2) {
            newParameterNameLayout.error = getString(R.string.too_short)
        }
        else {
            val value = Utils.toDoubleOrNull(newParameterValue.text.toString()) ?: 0.0
            val unit = newParameterUnitEdit.text.toString()
            viewModel?.saveNewParameter(name, value, unit, iconColor)
            (activity as MainActivity).showMainView()
        }
    }

    private fun selectColor(colorId: Int) {
        iconColor = MeasurementPresetTypeEnum.colors[colorId]
        newParameterIcon.setBackground(iconColor)
    }

}