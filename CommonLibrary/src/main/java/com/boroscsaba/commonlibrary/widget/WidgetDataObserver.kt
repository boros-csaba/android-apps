package com.boroscsaba.commonlibrary.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.database.ContentObserver
import android.os.Handler

class WidgetDataObserver(private val context: Context, handler: Handler, private val widgetClass: Class<*>): ContentObserver(handler) {

    override fun onChange(selfChange: Boolean) {
        val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, widgetClass))
        val widget = widgetClass.newInstance() as AppWidgetProvider
        widget.onUpdate(context, AppWidgetManager.getInstance(context), ids)
    }
}