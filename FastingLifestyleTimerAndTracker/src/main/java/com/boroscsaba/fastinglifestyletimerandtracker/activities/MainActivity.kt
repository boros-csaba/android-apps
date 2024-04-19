package com.boroscsaba.fastinglifestyletimerandtracker.activities

import com.boroscsaba.commonlibrary.activities.ActivityDrawerBase
import com.boroscsaba.fastinglifestyletimerandtracker.R
import com.boroscsaba.fastinglifestyletimerandtracker.view.ChartView
import com.boroscsaba.fastinglifestyletimerandtracker.view.HistoryView
import com.boroscsaba.fastinglifestyletimerandtracker.view.TimerView
import com.boroscsaba.fastinglifestyletimerandtracker.viewmodel.TimerViewModel

class MainActivity : ActivityDrawerBase(R.id.drawerLayout, R.mipmap.ic_launcher) {

    init {
        options.withViewModel(TimerViewModel::class.java)
                    .withNavigationDrawer()
                        .withViewPager()
                            .withBottomTabsLayout()
                            .addElement({ TimerView() }, R.drawable.baseline_timer_white_48)
                            .addElement({ HistoryView() }, R.drawable.ic_event_note_white_48dp)
                            .addElement({ ChartView() }, R.drawable.ic_show_chart_white_48dp)
    }
}
