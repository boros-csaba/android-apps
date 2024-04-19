package com.boroscsaba.bodymeasurementtracker.viewmodel

import android.app.Application
import android.content.Intent
import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import com.boroscsaba.bodymeasurementtracker.MeasurementPresetTypeEnum
import com.boroscsaba.bodymeasurementtracker.R
import com.boroscsaba.bodymeasurementtracker.dataaccess.ParameterRepository
import com.boroscsaba.bodymeasurementtracker.logic.MeasurementLogLogic
import com.boroscsaba.bodymeasurementtracker.logic.ParameterLogic
import com.boroscsaba.bodymeasurementtracker.model.MeasurementLogEntry
import com.boroscsaba.bodymeasurementtracker.model.Parameter
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.commonlibrary.activities.helpers.ViewModel
import com.boroscsaba.commonlibrary.settings.SettingsHelper
import com.boroscsaba.commonlibrary.viewelements.charts.ChartData

class MainViewModel(application: Application) : ViewModel(application) {

    val parameters = MutableLiveData<ArrayList<Parameter>>()
    val defaultUnit = MutableLiveData<String>()
    val chartDataSource = MutableLiveData<List<ChartData>>()
    val chartColor = MutableLiveData<Int>()
    private val lastHeightAndWeightMeasurements = MutableLiveData<ArrayList<MeasurementLogEntry>>()

    override fun initialize(intent: Intent?) {
        ParameterLogic(getApplication()).getDefaultUnit(defaultUnit)
        ParameterLogic(getApplication()).getParameters(parameters)
        MeasurementLogLogic(getApplication()).getLastHeightAndWeightMeasurements(lastHeightAndWeightMeasurements)
    }

    fun saveBmiChanges(newHeight: String, newWeight: String) {
        var weightParameter = parameters.value?.firstOrNull { p -> p.presetType == MeasurementPresetTypeEnum.WEIGHT }
        if (weightParameter == null) {
            weightParameter = Parameter(getApplication())
            weightParameter.presetType = MeasurementPresetTypeEnum.WEIGHT
            weightParameter.name = getApplication<ApplicationBase>().getString(MeasurementPresetTypeEnum.getDefaultTitleResource(MeasurementPresetTypeEnum.WEIGHT))
            weightParameter.color = MeasurementPresetTypeEnum.getDefaultIconColor(MeasurementPresetTypeEnum.WEIGHT)
            weightParameter.unit = if (defaultUnit.value == "cm") "kg" else "lbs"
            ParameterRepository(getApplication()).upsert(weightParameter, false)
        }
        saveValueChange(weightParameter.id, newWeight)

        var heightParameter = parameters.value?.firstOrNull { p -> p.presetType == MeasurementPresetTypeEnum.HEIGHT }
        if (heightParameter == null) {
            heightParameter = Parameter(getApplication())
            heightParameter.presetType = MeasurementPresetTypeEnum.HEIGHT
            heightParameter.name = getApplication<ApplicationBase>().getString(MeasurementPresetTypeEnum.getDefaultTitleResource(MeasurementPresetTypeEnum.HEIGHT))
            heightParameter.color = MeasurementPresetTypeEnum.getDefaultIconColor(MeasurementPresetTypeEnum.HEIGHT)
            heightParameter.unit = defaultUnit.value ?: "in"
            ParameterRepository(getApplication()).upsert(heightParameter, false)
        }
        saveValueChange(heightParameter.id, newHeight)
    }

    fun saveValueChange(parameterId: Int, valueString: String) {
        if (valueString.isNotEmpty()) {
            val value = Utils.toDoubleOrNull(valueString)
            if (value != null) {
                MeasurementLogLogic(getApplication()).saveQuickEditChange(parameterId, value)
            }
        }
    }

    fun updateDataSource(parameterId: Int) {
        val parameter = parameters.value?.firstOrNull { p -> p.id == parameterId } ?: parameters.value?.firstOrNull()
        if (parameter != null) {
            chartColor.value = Color.parseColor(parameter.color)
        }
        MeasurementLogLogic(getApplication()).getChartData(parameter?.id ?: parameterId, chartDataSource)
    }

    fun saveInitialInformation(weight: Double, weightUnit: String, height: Double, heightUnit: String, male: Boolean) {
        val weightParameter = parameters.value?.firstOrNull { p -> p.presetType == MeasurementPresetTypeEnum.WEIGHT } ?: Parameter(getApplication())
        weightParameter.presetType = MeasurementPresetTypeEnum.WEIGHT
        weightParameter.name = getApplication<ApplicationBase>().getString(MeasurementPresetTypeEnum.getDefaultTitleResource(MeasurementPresetTypeEnum.WEIGHT))
        weightParameter.color = MeasurementPresetTypeEnum.getDefaultIconColor(MeasurementPresetTypeEnum.WEIGHT)
        weightParameter.unit = weightUnit
        ParameterRepository(getApplication()).upsert(weightParameter, false)
        MeasurementLogLogic(getApplication()).saveQuickEditChange(weightParameter.id, weight)

        val heightParameter = parameters.value?.firstOrNull { p -> p.presetType == MeasurementPresetTypeEnum.HEIGHT } ?: Parameter(getApplication())
        heightParameter.presetType = MeasurementPresetTypeEnum.HEIGHT
        heightParameter.name = getApplication<ApplicationBase>().getString(MeasurementPresetTypeEnum.getDefaultTitleResource(MeasurementPresetTypeEnum.HEIGHT))
        heightParameter.color = MeasurementPresetTypeEnum.getDefaultIconColor(MeasurementPresetTypeEnum.HEIGHT)
        heightParameter.unit = heightUnit
        heightParameter.dashboardEnabled = false
        ParameterRepository(getApplication()).upsert(heightParameter, false)
        MeasurementLogLogic(getApplication()).saveQuickEditChange(heightParameter.id, height)

        SettingsHelper(getApplication()).saveSetting("SEX_SETTINGS", if (male) 1.0 else 0.0, getApplication<ApplicationBase>().getString(if (male) R.string.male else R.string.female))
    }

    fun saveNewParameter(name: String, value: Double, unit: String, color: String) {
        ParameterLogic(getApplication()).save(0, name, value, unit, color, MeasurementPresetTypeEnum.CUSTOM)
    }
}