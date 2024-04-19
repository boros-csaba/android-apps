package com.boroscsaba.commonlibrary.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.dataaccess.IObjectWithId
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.activities.ActivityBase
import com.boroscsaba.commonlibrary.adapters.SimpleRecyclerViewAdapter
import kotlinx.android.synthetic.main.widget_configure_layout.*

abstract class WidgetConfigureActivityBase<T: IObjectWithId>: ActivityBase() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    val values = MutableLiveData<ArrayList<T>>()
    abstract val widgetClass: Class<*>
    abstract val rowItemLayoutId: Int
    abstract fun getData()
    abstract fun onBindItemViewHolder(item: T, holder: androidx.recyclerview.widget.RecyclerView.ViewHolder)

    init {
        options.layout = R.layout.widget_configure_layout
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        values.observe(this, Observer { items ->
            if (items == null || items.isEmpty()) {
                widgetSelectLabel.setText(R.string.not_item_available)
            }
            else {
                initializeRecyclerView(items)
            }
        })
        getData()

        setResult(Activity.RESULT_CANCELED)
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
    }

    private fun initializeRecyclerView(values: ArrayList<T>) {
        if (recyclerView.adapter == null) {
            recyclerView.layoutManager = LinearLayoutManager(this)
            val adapter = SimpleRecyclerViewAdapter(ArrayList(values), rowItemLayoutId, { holder, item ->
                onBindItemViewHolder(item, holder)
                holder.itemView.setOnClickListener {
                    selectItem(item.id)
                }
            })
            recyclerView.adapter = adapter
        }
        else {
            @Suppress("UNCHECKED_CAST")
            val recyclerViewAdapter = recyclerView.adapter as SimpleRecyclerViewAdapter<T>
            recyclerViewAdapter.changeValues(values)
            recyclerViewAdapter.notifyDataSetChanged()
        }
    }

    private fun selectItem(itemId: Int) {
        val prefs = getSharedPreferences(getString(R.string.shared_preferences_name), 0).edit()
        prefs.putInt("appwidget_$appWidgetId", itemId)
        prefs.apply()

        WidgetConfigureActivityBase.itemId = itemId
        createWidget()
    }

    private fun createWidget() {
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()

        val ids = AppWidgetManager.getInstance(this).getAppWidgetIds(ComponentName(this, widgetClass))
        val widgetInstance = widgetClass.newInstance()
        if (widgetInstance is AppWidgetProvider) {
            widgetInstance.onUpdate(this, AppWidgetManager.getInstance(this), ids)
        }

        LoggingHelper.logEvent(this, "new_Widget")
    }

    companion object {
        private var itemId: Int = 0

        fun geItemId(context: Context, appWidgetId: Int): Int {
            val prefs = context.getSharedPreferences(context.getString(R.string.shared_preferences_name), 0)
            val prefItemId = prefs.getInt("appwidget_$appWidgetId", 0)
            return if (prefItemId == 0) {
                itemId
            } else {
                prefItemId
            }
        }

        fun deletePref(context: Context, appWidgetId: Int) {
            val prefs = context.getSharedPreferences(context.getString(R.string.shared_preferences_name), 0).edit()
            prefs.remove("appwidget_$appWidgetId")
            prefs.apply()
        }
    }
}