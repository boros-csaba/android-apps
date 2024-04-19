package com.boroscsaba.bodymeasurementtracker.logic

import android.content.Context
import androidx.lifecycle.MutableLiveData
import android.os.Handler
import android.os.Looper
import com.boroscsaba.bodymeasurementtracker.MeasurementPresetTypeEnum
import com.boroscsaba.bodymeasurementtracker.R
import com.boroscsaba.bodymeasurementtracker.dataaccess.*
import com.boroscsaba.bodymeasurementtracker.model.Parameter
import com.boroscsaba.bodymeasurementtracker.model.MeasurementLogEntry
import com.boroscsaba.commonlibrary.AsyncTask
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.commonlibrary.viewelements.charts.ChartData
import com.boroscsaba.dataaccess.DataObserverFactory

/**
* Created by boros on 2/10/2018.
*/
class MeasurementLogLogic(private val context: Context) {

    fun saveQuickEditChange(parameterId: Int, value: Double) {
        val measurementLogEntryRepository = MeasurementLogEntryRepository(context)
        val log = measurementLogEntryRepository.getObjects("parameterId = ? and julianday(date(log_date/1000,'unixepoch','localtime')) = julianday(date(${System.currentTimeMillis()}/1000,'unixepoch','localtime'))", arrayOf(parameterId.toString()), null).firstOrNull() ?: MeasurementLogEntry(context)
        if (log.id == 0) {
            log.logDate = System.currentTimeMillis()
            log.parameterId = parameterId
        }
        log.value = value
        measurementLogEntryRepository.upsert(log, false)
    }

    fun saveHistoricalChange(parameterId: Int, measurementId: Int, value: Double, date: Long) {
        if (parameterId <= 0) return
        val measurementLogEntryRepository = MeasurementLogEntryRepository(context)
        val log = measurementLogEntryRepository.getObjects("parameterId = ? and julianday(date(log_date/1000,'unixepoch','localtime')) = julianday(date($date/1000,'unixepoch','localtime'))", arrayOf(parameterId.toString()), null).firstOrNull() ?: MeasurementLogEntry(context)
        log.value = value
        log.logDate = date
        log.parameterId = parameterId
        measurementLogEntryRepository.upsert(log, false)
        if (log.id != measurementId) {
            measurementLogEntryRepository.delete(measurementId, false)
        }
    }

    fun saveParameterChanges(parameterId: Int, name: String, unit: String) {
        val parameterRepository = ParameterRepository(context)
        val parameter = parameterRepository.getObjectById(parameterId) ?: return
        parameter.name = name
        parameter.unit = unit
        parameterRepository.upsert(parameter, false)
        LoggingHelper.logEvent(context, "Parameter_name_change", "Name length", name.length.toString())
    }

    fun createInitialParameters(height: Double, isInch: Boolean, weight: Double, isLbs: Boolean) {
        val parameterRepository = ParameterRepository(context)

        val heightParameter = Parameter(context)
        heightParameter.presetType = MeasurementPresetTypeEnum.HEIGHT
        heightParameter.name = context.getString(R.string.height)
        heightParameter.unit = if (isInch) "in" else "cm"
        heightParameter.color = "#FF9800"
        parameterRepository.upsert(heightParameter, false)
        saveQuickEditChange(heightParameter.id, height)

        val weightParameter = Parameter(context)
        weightParameter.presetType = MeasurementPresetTypeEnum.WEIGHT
        weightParameter.name = context.getString(R.string.weight)
        weightParameter.unit = if (isLbs) "lbs" else "kg"
        weightParameter.color = "#2196F3"
        parameterRepository.upsert(weightParameter, false)
        saveQuickEditChange(weightParameter.id, weight)
    }

    fun getLastHeightAndWeightMeasurements(liveData: MutableLiveData<ArrayList<MeasurementLogEntry>>) {
        DataObserverFactory<ArrayList<MeasurementLogEntry>>(context).observe(ParameterMapper(context).getUri(), liveData) { getLastHeightAndWeightMeasurements() }
        DataObserverFactory<ArrayList<MeasurementLogEntry>>(context).observe(MeasurementLogEntryMapper(context).getUri(), liveData) { getLastHeightAndWeightMeasurements() }
        AsyncTask().execute({
            val result = getLastHeightAndWeightMeasurements()
            Handler(Looper.getMainLooper()).post{ liveData.value = result }
        })
    }

    private fun getLastHeightAndWeightMeasurements(): ArrayList<MeasurementLogEntry> {
        val result = ArrayList<MeasurementLogEntry>()
        val measurementLogEntryRepository = MeasurementLogEntryRepository(context)
        val parameters = ParameterRepository(context).getObjects(null, null, null)
        val heightParameter = parameters.firstOrNull { p -> p.presetType == MeasurementPresetTypeEnum.HEIGHT }
        val weightParameter = parameters.firstOrNull { p -> p.presetType == MeasurementPresetTypeEnum.WEIGHT }
        if (heightParameter != null) {
            val heightMeasurement = measurementLogEntryRepository.getObjects("parameterId = ?", arrayOf(heightParameter.id.toString()), "log_date desc").firstOrNull()
            if (heightMeasurement != null) {
                heightMeasurement.parameter = heightParameter
                result.add(heightMeasurement)
            }
        }
        if (weightParameter != null) {
            val weightMeasurement = measurementLogEntryRepository.getObjects("parameterId = ?", arrayOf(weightParameter.id.toString()), "log_date desc").firstOrNull()
            if (weightMeasurement != null) {
                weightMeasurement.parameter = weightParameter
                result.add(weightMeasurement)
            }
        }
        return result
    }

    fun deleteMeasurement(measurementId: Int) {
        MeasurementLogEntryRepository(context).delete(measurementId, false)
    }

    fun getChartData(parameterId: Int, liveData: MutableLiveData<List<ChartData>>) {
        DataObserverFactory<List<ChartData>>(context).observe(MeasurementLogEntryMapper(context).getUri(), liveData) { getChartData(parameterId) }
        AsyncTask().execute({
            val result = getChartData(parameterId)
            Handler(Looper.getMainLooper()).post{ liveData.value = result }
        })
    }

    private fun getChartData(parameterId: Int): List<ChartData> {
        return MeasurementLogEntryRepository(context)
                .getObjects("parameterId = ?", arrayOf(parameterId.toString()), null)
                .map{ p -> ChartData(p.logDate, p.value) }
                .sortedBy { p -> p.date }
    }

}