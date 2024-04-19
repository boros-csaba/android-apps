package com.boroscsaba.bodymeasurementtracker.widget

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.boroscsaba.bodymeasurementtracker.R
import com.boroscsaba.bodymeasurementtracker.logic.ParameterLogic
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.commonlibrary.splashscreen.SplashScreenActivity
import com.boroscsaba.commonlibrary.widget.WidgetBase
import com.boroscsaba.commonlibrary.widget.WidgetConfigureActivityBase
import kotlin.math.abs

class Widget: WidgetBase() {

    override val widgetLayoutId: Int = R.layout.widget_layout

    override fun addOnClickListeners(context: Context, views: RemoteViews, appWidgetId: Int) {
        val openAppIntent = Intent(context, SplashScreenActivity::class.java)
        val openAppPendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        views.setOnClickPendingIntent(R.id.openApp, openAppPendingIntent)
    }

    override fun fillViews(context: Context, views: RemoteViews, appWidgetId: Int) {
        val itemId = WidgetConfigureActivityBase.geItemId(context, appWidgetId)
        if (itemId <= 0) return
        val parameter = ParameterLogic(context).getParameter(itemId)
        if (parameter == null) {
            //views.setTextViewText(R.id.percentage_widget_textView, "Goal has been removed!")
        }
        else {
            views.setTextViewText(R.id.parameterName, parameter.name)
            views.setTextViewText(R.id.lastValueUnit, parameter.unit)
            val lastEntry = parameter.measurements.firstOrNull()
            if (lastEntry != null) {
                views.setTextViewText(R.id.lastValue, String.format("%.1f", lastEntry.value))
                val secondLastEntry = parameter.measurements.firstOrNull { d -> d.value != lastEntry.value }
                if (secondLastEntry != null) {
                    val change = lastEntry.value - secondLastEntry.value
                    views.setTextViewText(R.id.changeValue, String.format("%.1f %s", abs(change), parameter.unit))
                    views.setTextViewText(R.id.changePeriod, String.format("in ${(lastEntry.logDate - secondLastEntry.logDate) / 1000 / 60 / 60 / 24 + 1} days"))

                    val arrowBitmap = Utils.getBitmapFromVectorDrawable(R.drawable.ic_arrow_downward_white_48dp, context)
                    if (arrowBitmap != null) {
                        if (change > 0) {
                            Utils.rotate(arrowBitmap, 180f)
                        }
                        views.setImageViewBitmap(R.id.changeDirection, arrowBitmap)
                    }
                }
            }

            /*val decimalFormat = DecimalFormat("0.##")
            val percentage = (goal.initialAmount + goal.transactions.sumByDouble{ t -> t.amount }) * 100 / goal.targetAmount
            views.setTextViewText(R.id.progressBar_Text_TextView, decimalFormat.format(percentage) + "%")
            val progress = Math.floor(percentage).toInt()
            views.setInt(R.id.progressBar, "setImageLevel", progress * 100)*/
        }
    }

}