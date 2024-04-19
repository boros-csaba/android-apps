package com.boroscsaba.bodymeasurementtracker.logic

import android.content.Context
import androidx.lifecycle.MutableLiveData
import android.os.Handler
import android.os.Looper
import com.boroscsaba.bodymeasurementtracker.MeasurementPresetTypeEnum
import com.boroscsaba.bodymeasurementtracker.R
import com.boroscsaba.bodymeasurementtracker.dataaccess.MeasurementLogEntryMapper
import com.boroscsaba.bodymeasurementtracker.dataaccess.MeasurementLogEntryRepository
import com.boroscsaba.bodymeasurementtracker.dataaccess.ParameterMapper
import com.boroscsaba.bodymeasurementtracker.dataaccess.ParameterRepository
import com.boroscsaba.bodymeasurementtracker.model.Parameter
import com.boroscsaba.commonlibrary.AsyncTask
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.dataaccess.DataObserverFactory

class ParameterLogic(private val context: Context) {

    fun save(parameterId: Int, name: String, value: Double, unit: String, iconColor: String, presetType: MeasurementPresetTypeEnum): Int {
        val parameter: Parameter
        val isNew = parameterId == 0
        parameter = if (isNew) {
            LoggingHelper.logEvent(context, "New_Parameter", "Name", name)
            Parameter(context)
        }
        else {
            ParameterRepository(context).getObjectById(parameterId) ?: return 0
        }
        parameter.name = name
        parameter.unit = unit
        parameter.color = iconColor
        parameter.presetType = presetType
        ParameterRepository(context).upsert(parameter, false)

        val measurementLogLogic = MeasurementLogLogic(context)
        measurementLogLogic.saveQuickEditChange(parameter.id, value)

        return parameter.id
    }

    fun getPresetParameters(presetParameters: MutableLiveData<Array<Parameter>>) {
        AsyncTask().execute({
            val defaultUnit = getDefaultUnit()
            val height = Parameter(context)
            height.presetType = MeasurementPresetTypeEnum.HEIGHT
            height.name = context.getString(R.string.height)
            height.color = "#FF9800"
            height.unit = defaultUnit
            val weight = Parameter(context)
            weight.presetType = MeasurementPresetTypeEnum.WEIGHT
            weight.name = context.getString(R.string.weight)
            weight.color = "#2196F3"
            weight.unit = if (defaultUnit == "cm") "kg" else "lbs"
            val waist = Parameter(context)
            waist.presetType = MeasurementPresetTypeEnum.WAIST
            waist.name = context.getString(R.string.waist)
            waist.color = "#795548"
            waist.unit = defaultUnit
            val hips = Parameter(context)
            hips.presetType = MeasurementPresetTypeEnum.HIPS
            hips.name = context.getString(R.string.hips)
            hips.color = "#009688"
            hips.unit = defaultUnit

            val usedPresetParameters = ParameterRepository(context).getObjects("preset_type <> ?", arrayOf(MeasurementPresetTypeEnum.CUSTOM.toString()), null)
            val presets = arrayOf(height, weight, waist, hips).filter { p ->
                !usedPresetParameters.any { u -> u.presetType == p.presetType }
            }.toTypedArray()

            Handler(Looper.getMainLooper()).post { presetParameters.value = presets }
        })
    }

    fun getParameter(parameterId: Int, liveData: MutableLiveData<Parameter?>) {
        DataObserverFactory<Parameter?>(context).observe(ParameterMapper(context).getUri(parameterId), liveData) { getParameter(parameterId) }
        DataObserverFactory<Parameter?>(context).observe(MeasurementLogEntryMapper(context).getUri(), liveData) { getParameter(parameterId) }
        AsyncTask().execute({
            val result = getParameter(parameterId)
            Handler(Looper.getMainLooper()).post { liveData.value = result }
        })
    }

    fun getParameter(parameterId: Int): Parameter? {
        val parameter = ParameterRepository(context).getObjectById(parameterId) ?: return null
        val measurementLogEntryRepository = MeasurementLogEntryRepository(context)
        parameter.measurements.addAll(measurementLogEntryRepository.getObjects("parameterId = ?", arrayOf(parameter.id.toString()), "log_date desc"))
        parameter.measurements.forEach { measurement ->
            measurement.parameter = parameter
        }
        return parameter
    }

    fun getParameters(liveData: MutableLiveData<ArrayList<Parameter>>) {
        DataObserverFactory<ArrayList<Parameter>>(context).observe(ParameterMapper(context).getUri(), liveData) { getParameters() }
        DataObserverFactory<ArrayList<Parameter>>(context).observe(MeasurementLogEntryMapper(context).getUri(), liveData) { getParameters() }
        AsyncTask().execute({
            val result = getParameters()
            Handler(Looper.getMainLooper()).post { liveData.value = result }
        })
    }

    fun getParameters(): ArrayList<Parameter> {
        val parameters = ParameterRepository(context).getObjects(null, null, null)
        parameters.sortByDescending { p -> p.modifiedDate }
        val measurementLogEntryRepository = MeasurementLogEntryRepository(context)
        parameters.forEach { parameter ->
            parameter.measurements.addAll(measurementLogEntryRepository.getObjects("parameterId = ?", arrayOf(parameter.id.toString()), "log_date desc"))
            parameter.measurements.forEach { measurement ->
                measurement.parameter = parameter
            }
        }
        return parameters
    }

    fun getDefaultUnit(liveData: MutableLiveData<String>) {
        AsyncTask().execute({
            val result = getDefaultUnit()
            Handler(Looper.getMainLooper()).post { liveData.value = result }
        })
    }

    private fun getDefaultUnit(): String {
        val parameters = getParameters()
        parameters.sortByDescending { p -> p.modifiedDate }
        parameters.forEach { p ->
            if (p.unit == "in") return "in"
            if (p.unit == "cm") return "cm"
        }
        return "in"
    }

    fun deleteParameter(parameterId: Int) {
        AsyncTask().execute({
            val measurementLogEntryRepository = MeasurementLogEntryRepository(context)
            val entries = measurementLogEntryRepository.getObjects("parameterId = ?", arrayOf(parameterId.toString()), null)
            entries.forEach { e -> measurementLogEntryRepository.delete(e.id, false) }
            ParameterRepository(context).delete(parameterId, false)
        })
    }
}