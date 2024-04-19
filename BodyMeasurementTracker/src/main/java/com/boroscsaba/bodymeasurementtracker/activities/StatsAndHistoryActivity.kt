package com.boroscsaba.bodymeasurementtracker.activities

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.boroscsaba.bodymeasurementtracker.R
import com.boroscsaba.bodymeasurementtracker.view.ChartView
import com.boroscsaba.bodymeasurementtracker.view.MeasurementHistoryView
import com.boroscsaba.bodymeasurementtracker.viewmodel.MainViewModel
import com.boroscsaba.commonlibrary.activities.ActivityBase

class StatsAndHistoryActivity: ActivityBase() {

    init {
        options.withViewModel(MainViewModel::class.java)
                    .withNoNavigationDrawer()
                       .withViewPager()
                           .withBottomTabsLayout()
                           .addElement({ MeasurementHistoryView() }, R.drawable.ic_event_note_white_48dp)
                           .addElement({ ChartView() }, R.drawable.ic_show_chart_white_48dp)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}