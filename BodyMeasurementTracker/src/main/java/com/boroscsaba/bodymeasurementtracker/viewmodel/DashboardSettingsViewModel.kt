package com.boroscsaba.bodymeasurementtracker.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.boroscsaba.bodymeasurementtracker.logic.DashboardLogic
import com.boroscsaba.bodymeasurementtracker.model.DashboardSettingsItem
import com.boroscsaba.commonlibrary.activities.helpers.ViewModel
import com.boroscsaba.dataaccess.AsyncTask

class DashboardSettingsViewModel(application: Application) : ViewModel(application) {

    val dashboardSettingsItems = MutableLiveData<ArrayList<DashboardSettingsItem>>()

    override fun initialize(intent: Intent?) {
        DashboardLogic(getApplication()).getDashboardSettingsItems(dashboardSettingsItems)
    }

    fun saveChanges(values: ArrayList<DashboardSettingsItem>) {
        AsyncTask().execute({
            DashboardLogic(getApplication()).saveChanges(values)
        })
    }
}