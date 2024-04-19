package com.boroscsaba.englishirregularverbsmemorizer.activities

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.boroscsaba.commonlibrary.activities.ActivityBase
import com.boroscsaba.commonlibrary.activities.helpers.TabsPagerFragmentAdapter
import com.boroscsaba.englishirregularverbsmemorizer.R
import com.boroscsaba.englishirregularverbsmemorizer.view.ProgressChartView
import com.boroscsaba.englishirregularverbsmemorizer.view.VerbsListView
import com.boroscsaba.englishirregularverbsmemorizer.viewmodel.StatsViewModel
import kotlinx.android.synthetic.main.activity_stats.*

class StatsActivity : ActivityBase() {

    init {
        options.layout = R.layout.activity_stats
        options.toolbarId = R.id.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ViewModelProviders.of(this).get(StatsViewModel::class.java).initialize()
        setupTabLayout()
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) { }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    searchContainer.visibility = View.VISIBLE
                }
                else {
                    searchContainer.visibility = View.GONE
                }
            }
        })

    }

    override fun onBackPressed() {
        if (viewPager.currentItem != 0) {
            viewPager.setCurrentItem(0, true)
            return
        }
        super.onBackPressed()
    }

    private fun setupTabLayout() {
        val adapter = TabsPagerFragmentAdapter(supportFragmentManager, 1) { position ->
            when (position) {
                0 -> VerbsListView()
                //1 -> ProgressChartView()
                else -> VerbsListView()
            }
        }
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_assignment_black_48dp)?.tag = "ProgressChart"
        tabLayout.getTabAt(0)?.icon?.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN)
        //tabLayout.getTabAt(1)?.setIcon(R.drawable.ic_show_chart_white_48dp)?.tag = "ProgressChart"
        //tabLayout.getTabAt(1)?.icon?.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN)
    }
}