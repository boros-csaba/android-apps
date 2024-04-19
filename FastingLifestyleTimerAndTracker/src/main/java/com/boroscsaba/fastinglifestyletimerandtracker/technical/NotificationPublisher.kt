package com.boroscsaba.fastinglifestyletimerandtracker.technical

import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.commonlibrary.helpers.NotificationHelper
import com.boroscsaba.commonlibrary.settings.SettingsHelper
import com.boroscsaba.fastinglifestyletimerandtracker.R
import com.boroscsaba.fastinglifestyletimerandtracker.activities.MainActivity


class NotificationPublisher : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        LoggingHelper().initialize(context)

        if (!context.getSharedPreferences("Preferences", Context.MODE_PRIVATE).getBoolean(SettingsHelper.NOTIFICATION_SETTINGS, true)) {
            return
        }
        LoggingHelper.logEvent(context, "Timer_almost_over_notification")

        NotificationHelper(context).showNotification(
                "Your eating window is almost over!",
                "Only 15 more minutes left to eat, hurry up!",
                R.drawable.ic_noticiation_icon,
                MainActivity::class.java,
                NotificationHelper.NotificationTypeEnum.TIMED, isInactivityNotification = false)
    }
}