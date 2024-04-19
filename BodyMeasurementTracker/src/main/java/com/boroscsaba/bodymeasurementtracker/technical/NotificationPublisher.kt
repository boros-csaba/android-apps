package com.boroscsaba.bodymeasurementtracker.technical

import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import com.boroscsaba.bodymeasurementtracker.MeasurementPresetTypeEnum
import com.boroscsaba.bodymeasurementtracker.R
import com.boroscsaba.bodymeasurementtracker.activities.MainActivity
import com.boroscsaba.bodymeasurementtracker.logic.ParameterLogic
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.commonlibrary.helpers.NotificationHelper


class NotificationPublisher : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        LoggingHelper().initialize(context)

        val notificationHelper = NotificationHelper(context)
        if (!notificationHelper.canShowWeeklyNotification()) {
            return
        }

        val title = "Don`t give up your fitness goals!"
        val description: String

        val parameters = ParameterLogic(context).getParameters()
        description = if (parameters.isEmpty()) {
            "Start by adding your first measurements."
        }
        else {
            val oldestParameter =
                    parameters.filter { p -> p.presetType != MeasurementPresetTypeEnum.HEIGHT && p.presetType != MeasurementPresetTypeEnum.BMI }
                            .sortedBy { p -> p.measurements.firstOrNull()?.logDate ?: 0 }
                            .firstOrNull()
            if (oldestParameter == null) {
                "Start by adding your first measurements."
            } else {
                "You haven't measured your ${oldestParameter.name} in a while!"
            }
        }

        notificationHelper.showNotification(
                title,
                description,
                R.drawable.ic_ruler,
                MainActivity::class.java,
                NotificationHelper.NotificationTypeEnum.WEEKLY,
                isInactivityNotification = true)
    }
}