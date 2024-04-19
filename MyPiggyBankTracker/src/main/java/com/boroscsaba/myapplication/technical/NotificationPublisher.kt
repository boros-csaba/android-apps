package com.boroscsaba.myapplication.technical

import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.commonlibrary.helpers.NotificationHelper
import com.boroscsaba.myapplication.R
import com.boroscsaba.myapplication.view.GoalDetailsView
import com.boroscsaba.myapplication.dataAccess.GoalRepository
import com.boroscsaba.myapplication.logic.GoalLogic


class NotificationPublisher : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        LoggingHelper().initialize(context)

        val notificationHelper = NotificationHelper(context)
        if (!notificationHelper.canShowDailyNotification()) {
            return
        }

        val goals = GoalLogic(context).getActiveGoals().filter { goal -> goal.notificationEnabled }
        val today = getDays(System.currentTimeMillis())
        var isInactiveNotification = false

        val dueToday = goals.firstOrNull { goal -> goal.dueDate > 1 && getDays(goal.dueDate) == today }
        val dueTomorrow = goals.firstOrNull { goal -> goal.dueDate > 1 && getDays(goal.dueDate) - 1 == today }
        val overdue = goals.firstOrNull { goal -> goal.dueDate > 1 && getDays(goal.dueDate) < today }
        val inactive = goals.firstOrNull { goal ->
            goal.transactions.sortBy { t -> t.createdDate }
            getDays(goal.modifiedDate) + 14 <= today && (goal.transactions.firstOrNull() == null || getDays(goal.transactions.first().createdDate) + 14 <= today)  }

        val goalId: Int
        val notificationText: String
        val notificationType: NotificationHelper.NotificationTypeEnum
        when {
            dueToday != null -> {
                goalId = dueToday.id
                notificationText = dueToday.title + " is due today!"
                LoggingHelper.logEvent(context, "Due_today_notification")
                notificationType = NotificationHelper.NotificationTypeEnum.DAILY
            }
            dueTomorrow != null -> {
                goalId = dueTomorrow.id
                notificationText = dueTomorrow.title + " is due tomorrow!"
                LoggingHelper.logEvent(context, "Due_tomorrow_notification")
                notificationType = NotificationHelper.NotificationTypeEnum.DAILY
            }
            overdue != null -> {
                goalId = overdue.id
                notificationText = overdue.title + " is overdue!"
                LoggingHelper.logEvent(context, "Overdue_notification")
                notificationType = NotificationHelper.NotificationTypeEnum.DAILY
            }
            inactive != null -> {
                goalId = inactive.id
                notificationText = "You haven`t saved for " + inactive.title + " in a while!"
                GoalRepository(context).upsert(inactive, false)
                LoggingHelper.logEvent(context, "Inactive_notification")
                isInactiveNotification = true
                notificationType = NotificationHelper.NotificationTypeEnum.WEEKLY
            }
            else -> return
        }

        if (notificationType == NotificationHelper.NotificationTypeEnum.WEEKLY) {
            if (!notificationHelper.canShowWeeklyNotification()) {
                return
            }
        }

        notificationHelper.showNotification(
                "Don`t forget your goals!",
                notificationText,
                R.mipmap.ic_launcher,
                GoalDetailsView::class.java,
                notificationType, isInactiveNotification, "GOAL_ID", goalId)
    }

    private fun getDays(date: Long): Int {
        return (date / (24 * 60 * 60 * 1000)).toInt()
    }
}