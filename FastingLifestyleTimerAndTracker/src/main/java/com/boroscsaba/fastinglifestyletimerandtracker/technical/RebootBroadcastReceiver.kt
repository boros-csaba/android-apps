package com.boroscsaba.fastinglifestyletimerandtracker.technical

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.commonlibrary.helpers.NotificationHelper

class RebootBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null && intent.action == "android.intent.action.BOOT_COMPLETED") {
            NotificationHelper.scheduleNotifications(context, NotificationPublisher::class.java)
            LoggingHelper.logEvent(context, "REBOOT_NOTIFICATION")
        }
    }
}