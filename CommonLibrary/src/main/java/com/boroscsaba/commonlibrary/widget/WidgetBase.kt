package com.boroscsaba.commonlibrary.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.boroscsaba.commonlibrary.LoggingHelper

abstract class WidgetBase : AppWidgetProvider() {

    abstract val widgetLayoutId: Int

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, widgetLayoutId)
            addOnClickListeners(context, views, appWidgetId)
            fillViews(context, views, appWidgetId)
            appWidgetManager.updateAppWidget(appWidgetId, views)

            val fabricHelper = LoggingHelper()
            fabricHelper.initialize(context)
            LoggingHelper.logEvent(context, "widget_updated")
        }
    }

    abstract fun addOnClickListeners(context: Context, views: RemoteViews, appWidgetId: Int)

    abstract fun fillViews(context: Context, views: RemoteViews, appWidgetId: Int)

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            WidgetConfigureActivityBase.deletePref(context, appWidgetId)
            val fabricHelper = LoggingHelper()
            fabricHelper.initialize(context)
            LoggingHelper.logEvent(context, "widget_deleted")
        }
    }

}