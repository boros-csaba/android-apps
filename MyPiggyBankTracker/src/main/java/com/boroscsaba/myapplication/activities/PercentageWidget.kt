package com.boroscsaba.myapplication.activities

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.boroscsaba.commonlibrary.splashscreen.SplashScreenActivity
import com.boroscsaba.commonlibrary.widget.WidgetBase
import com.boroscsaba.commonlibrary.widget.WidgetConfigureActivityBase

import com.boroscsaba.myapplication.R
import com.boroscsaba.myapplication.logic.GoalLogic

import java.text.DecimalFormat


class PercentageWidget : WidgetBase() {

    override val widgetLayoutId: Int = R.layout.percentage_widget

    override fun addOnClickListeners(context: Context, views: RemoteViews, appWidgetId: Int) {
        val itemId = WidgetConfigureActivityBase.geItemId(context, appWidgetId)
        val goal = GoalLogic(context).getGoalForWidget(itemId)

        val intent = if (goal != null) {
            Intent(context, EditTransactionActivity::class.java)
        }
        else {
            Intent(context, SplashScreenActivity::class.java)
        }
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
        intent.putExtra("GOAL_ID", itemId)
        val pendIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        views.setOnClickPendingIntent(R.id.percentage_widget_plus_button, pendIntent)

        val openAppIntent = Intent(context, SplashScreenActivity::class.java)
        val openAppPendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        views.setOnClickPendingIntent(R.id.percentageWidgetBarContainer, openAppPendingIntent)
    }

    override fun fillViews(context: Context, views: RemoteViews, appWidgetId: Int) {
        val itemId = WidgetConfigureActivityBase.geItemId(context, appWidgetId)
        val goal = GoalLogic(context).getGoalForWidget(itemId)
        if (goal == null) {
            views.setTextViewText(R.id.percentage_widget_textView, "Goal has been removed!")
        }
        else {
            views.setTextViewText(R.id.percentage_widget_textView, goal.title)
            val decimalFormat = DecimalFormat("0.##")
            val percentage = (goal.initialAmount + goal.transactions.sumByDouble{ t -> t.amount }) * 100 / goal.targetAmount
            views.setTextViewText(R.id.progressBar_Text_TextView, decimalFormat.format(percentage) + "%")
            val progress = Math.floor(percentage).toInt()
            views.setInt(R.id.progressBar, "setImageLevel", progress * 100)
        }
    }
}

