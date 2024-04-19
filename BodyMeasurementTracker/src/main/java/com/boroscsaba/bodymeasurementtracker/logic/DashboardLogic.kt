package com.boroscsaba.bodymeasurementtracker.logic

import android.app.Application
import androidx.lifecycle.MutableLiveData
import android.os.Handler
import android.os.Looper
import com.boroscsaba.bodymeasurementtracker.MeasurementPresetTypeEnum
import com.boroscsaba.bodymeasurementtracker.dataaccess.ParameterMapper
import com.boroscsaba.bodymeasurementtracker.dataaccess.ParameterRepository
import com.boroscsaba.bodymeasurementtracker.model.DashboardBlockTypeEnum
import com.boroscsaba.bodymeasurementtracker.model.DashboardSettingsItem
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.dataaccess.AsyncTask
import com.boroscsaba.dataaccess.DataObserverFactory

class DashboardLogic(private val application: Application) {

    fun getDashboardSettingsItems(liveData: MutableLiveData<ArrayList<DashboardSettingsItem>>) {
        DataObserverFactory<ArrayList<DashboardSettingsItem>>(application).observe(ParameterMapper(application).getUri(), liveData) { getDashboardSettingsItems() }
        AsyncTask().execute({
            val result = getDashboardSettingsItems()
            Handler(Looper.getMainLooper()).post { liveData.value = result }
        })
    }

    private fun getDashboardSettingsItems(): ArrayList<DashboardSettingsItem> {
        val parameters = ParameterRepository(application).getObjects(null, null, null)
        val result = arrayListOf<DashboardSettingsItem>()
        var id = 0
        parameters.forEach { parameter ->
            val setting =  DashboardSettingsItem(id++,
                    parameter.name,
                    DashboardBlockTypeEnum.CurrentValue,
                    parameter.dashboardEnabled,
                    parameter.dashboardEnabled,
                    parameter.dashboardOrder,
                    parameter.dashboardOrder,
                    if (parameter.presetType == MeasurementPresetTypeEnum.CUSTOM) parameter.name[0].toString() else "",
                    parameter.presetType,
                    parameter.color,
                    parameter)
            result.add(setting)
        }
        return result
    }

    fun saveChanges(values: ArrayList<DashboardSettingsItem>) {
        LoggingHelper.logEvent(application, "Save_Dashboard_settings")
        for (i in 0 until values.size) {
            values[i].order = i + 1
        }
        values.sortBy { v -> v.id }

        val parameterRepository = ParameterRepository(application)
        for (i in 0 until values.size) {
            if (values[i].enabledInitialValue != values[i].enabled || values[i].orderInitialValue != values[i].order) {
                if (values[i].type == DashboardBlockTypeEnum.CurrentValue) {
                    values[i].parameter.dashboardOrder = values[i].order
                    values[i].parameter.dashboardEnabled = values[i].enabled
                }
                parameterRepository.upsert(values[i].parameter, false)
            }
        }
    }
}