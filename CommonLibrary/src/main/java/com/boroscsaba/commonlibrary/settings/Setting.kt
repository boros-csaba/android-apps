package com.boroscsaba.commonlibrary.settings

import android.content.Context

open class Setting(private val context: Context, val visible: Boolean, val name: String, val defaultValue: Double, val defaultValueDisplay: String, val titleResourceId: Int, val descriptionResourceId: Int?) {
    var option1: SettingOption? = null
    var option2: SettingOption? = null
    var option3: SettingOption? = null
    var showValueEditor = false
    var showNumberPicker = false
    var defaultUnit = ""
    val unitArray = ArrayList<String>()
    var minValue: Int? = null
    var maxValue: Int? = null

    fun setOption1(display: String, value: Double) {
        option1 = SettingOption(context)
        option1?.displayValue = display
        option1?.value = value
    }

    fun setOption2(display: String, value: Double) {
        option2 = SettingOption(context)
        option2?.displayValue = display
        option2?.value = value
    }

    fun setOption3(display: String, value: Double) {
        option3 = SettingOption(context)
        option3?.displayValue = display
        option3?.value = value
    }

    fun showValueEditor(defaultUnit: String, units: Array<String>) {
        showValueEditor = true
        this.defaultUnit = defaultUnit
        unitArray.addAll(units)
    }

    fun showValueEditor(minValue: Int, maxValue: Int, unit: String) {
        showNumberPicker = true
        this.defaultUnit = unit
        this.minValue = minValue
        this.maxValue = maxValue
    }
}