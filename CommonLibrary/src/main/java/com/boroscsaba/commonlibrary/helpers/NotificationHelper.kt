package com.boroscsaba.commonlibrary.helpers

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.settings.SettingsHelper
import java.util.*

class NotificationHelper(private val context: Context) {

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("NotificationChannel", "Notifications", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = context.getString(R.string.notification_settings_description)
            }
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun canShowDailyNotification(): Boolean {
        val sharedPreferences = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val lastSent = sharedPreferences.getLong("LAST_SENT_DAILY_NOTIFICATION", 0)
        val lastSentDateIsValued = lastSent + 1000 * 60 * 60 * 20 < System.currentTimeMillis()
        val notificationEnabled = sharedPreferences.getBoolean(SettingsHelper.NOTIFICATION_SETTINGS, true)
        if (!lastSentDateIsValued) {
            LoggingHelper.logEvent(context, "Notification_already_shown!")
        }
        return lastSentDateIsValued && notificationEnabled
    }

    fun canShowWeeklyNotification(): Boolean {
        val sharedPreferences = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val lastSent = sharedPreferences.getLong("LAST_SENT_WEEKLY_NOTIFICATION", 0)
        val lastSentDateIsValued = lastSent + 1000 * 60 * 60 * 20 < System.currentTimeMillis()
        val notificationEnabled = sharedPreferences.getBoolean(SettingsHelper.NOTIFICATION_SETTINGS, true)
        if (!lastSentDateIsValued) {
            LoggingHelper.logEvent(context, "Notification_already_shown!")
        }
        return lastSentDateIsValued && notificationEnabled
    }

    fun showNotification(title: String, contentText: String, icon: Int, activityClass: Class<*>, notificationType: NotificationTypeEnum, isInactivityNotification: Boolean, extraName: String? = null, extraValue: Int? = null) {
        if (isInactivityNotification) {
            val sharedPreferences = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
            val lastSent = sharedPreferences.getLong("LAST_SENT_INACTIVITY_NOTIFICATION", 0)
            if (lastSent + 1000 * 60 * 60 * 24 * 7 > System.currentTimeMillis()) {
                return
            }
        }

        createNotificationChannel()
        val builder = NotificationCompat.Builder(context, "NotificationChannel")
                .setContentTitle(title)
                .setContentText(contentText)
                .setAutoCancel(true)
                .setSmallIcon(icon)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        val openAppIntent = Intent(context, activityClass)
        if (extraName != null && extraValue != null) {
            openAppIntent.putExtra(extraName, extraValue)
        }
        val openAppPendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(openAppPendingIntent)

        val notification = builder.build()
        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
        LoggingHelper.logEvent(context, "Notification_${title.replace(" ", "_")}")
        if (notificationType != NotificationTypeEnum.TIMED) {
            val editor = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE).edit()
            if (notificationType == NotificationTypeEnum.DAILY) {
                editor.putLong("LAST_SENT_DAILY_NOTIFICATION", System.currentTimeMillis())
            }
            else {
                editor.putLong("LAST_SENT_WEEKLY_NOTIFICATION", System.currentTimeMillis())
            }
            editor.apply()
        }
    }

    enum class NotificationTypeEnum {
        TIMED,
        DAILY,
        WEEKLY
    }

    companion object {
        fun scheduleNotifications(context: Context, notificationPublisher: Class<*>) {
            val calendar = Calendar.getInstance()
            val currentDate = calendar.timeInMillis
            calendar.set(Calendar.HOUR_OF_DAY, 15)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            if (calendar.timeInMillis < currentDate) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            val notificationIntent = Intent(context, notificationPublisher)
            val pendingIntent = PendingIntent.getBroadcast(context, 1, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
        }
    }
}