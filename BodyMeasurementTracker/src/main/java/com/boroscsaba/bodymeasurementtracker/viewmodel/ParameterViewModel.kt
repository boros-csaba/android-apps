package com.boroscsaba.bodymeasurementtracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.boroscsaba.bodymeasurementtracker.logic.MeasurementLogLogic
import com.boroscsaba.bodymeasurementtracker.logic.ParameterLogic
import com.boroscsaba.bodymeasurementtracker.model.Parameter
import com.boroscsaba.commonlibrary.Utils

class ParameterViewModel(application: Application) : AndroidViewModel(application) {

    val parameter = MutableLiveData<Parameter?>()
    val changeViewCounter = MutableLiveData<Int>()

    fun initialize(parameterId: Int) {
        ParameterLogic(getApplication()).getParameter(parameterId, parameter)
    }

    fun saveChanges(newParameterName: String, newUnit: String) {
        val parameter = parameter.value ?: return
        val unit = if (newUnit.isEmpty()) parameter.unit else newUnit
        if (parameter.name != newParameterName || parameter.unit != unit) {
            MeasurementLogLogic(getApplication()).saveParameterChanges(parameter.id, newParameterName, unit)
        }
    }

    fun saveValueChange(valueString: String) {
        val parameter = parameter.value ?: return
        if (valueString.isNotEmpty()) {
            val value = Utils.toDoubleOrNull(valueString)
            if (value != null) {
                MeasurementLogLogic(getApplication()).saveQuickEditChange(parameter.id, value)
            }
        }
    }

    fun deleteParameter() {
        val parameterId = parameter.value?.id ?: return
        ParameterLogic(getApplication()).deleteParameter(parameterId)
    }

    fun saveHistoryEdit(measurementId: Int, valueString: String, date: Long) {
        val parameterId = parameter.value?.id ?: return
        val value = Utils.toDoubleOrNull(valueString)
        if (value != null && date > 0) {
            MeasurementLogLogic(getApplication()).saveHistoricalChange(parameterId, measurementId, value, date)
        }
    }

    fun deleteMeasurement(measurementId: Int) {
        MeasurementLogLogic(getApplication()).deleteMeasurement(measurementId)
    }

    fun changeView() {
        changeViewCounter.value = (changeViewCounter.value ?: 0) + 1
    }
}