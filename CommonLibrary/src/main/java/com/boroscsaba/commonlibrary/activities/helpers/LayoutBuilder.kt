package com.boroscsaba.commonlibrary.activities.helpers

import android.content.Intent
import android.graphics.PorterDuff
import android.view.View
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.boroscsaba.commonlibrary.*
import com.boroscsaba.commonlibrary.activities.ActivityBase
import com.boroscsaba.commonlibrary.activities.ActivityOptions
import com.boroscsaba.commonlibrary.activities.FragmentBase
import com.boroscsaba.commonlibrary.activities.historyView.HistoryFragment
import com.boroscsaba.commonlibrary.views.TabLayout
import com.boroscsaba.dataaccess.PersistentObject

class LayoutBuilder(private val activity: ActivityBase, private val intent: Intent) {

    private val layoutConfig = activity.options.layoutConfig

    fun build() {
        if (layoutConfig.viewModelClass != null) {
            activity.viewModel = ViewModelProviders.of(activity).get(Utils.performUncheckableCase<Class<ViewModel>>(layoutConfig.viewModelClass as Any))
        }
        if (activity.options.layout == null) {
            activity.setContentView(layoutConfig.baseLayoutId!!)
            buildLayout()
        }
        activity.viewModel?.initialize(activity.intent)
    }

    private fun buildLayout() {
        val baseContainer = activity.findViewById<ConstraintLayout>(R.id.baseContainer)
        if (layoutConfig.hasViewPager) {
            val viewPager = createViewPager()
            baseContainer?.addView(viewPager)

            val constraintSet = ConstraintSet()
            constraintSet.clone(baseContainer)
            constraintSet.constrainHeight(viewPager.id, ConstraintSet.MATCH_CONSTRAINT)
            constraintSet.connect(viewPager.id, ConstraintSet.TOP, R.id.appBarLayout, ConstraintSet.BOTTOM)
            constraintSet.connect(viewPager.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            constraintSet.connect(viewPager.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

            if (layoutConfig.hasBottomTabLayout) {
                val tabLayout = createTabLayout()
                baseContainer?.addView(tabLayout)
                constraintSet.constrainHeight(tabLayout.id, ConstraintSet.WRAP_CONTENT)
                constraintSet.connect(viewPager.id, ConstraintSet.BOTTOM, tabLayout.id, ConstraintSet.TOP)
                constraintSet.connect(tabLayout.id, ConstraintSet.TOP, viewPager.id, ConstraintSet.BOTTOM)
                constraintSet.connect(tabLayout.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                constraintSet.connect(tabLayout.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                constraintSet.connect(tabLayout.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                setupTabLayout(tabLayout, viewPager)
            }
            constraintSet.applyTo(baseContainer)

            val startIndex = intent.getIntExtra("pageIndex", 0)
            viewPager.setCurrentItem(startIndex ,false)
        }
        else if (layoutConfig.contentLayoutId != null) {
            val scrollView = createScrollView()
            baseContainer?.addView(scrollView)
            val constraintSet = ConstraintSet()
            constraintSet.clone(baseContainer)
            constraintSet.constrainHeight(scrollView.id, ConstraintSet.MATCH_CONSTRAINT)
            constraintSet.connect(scrollView.id, ConstraintSet.TOP, R.id.appBarLayout, ConstraintSet.BOTTOM)
            constraintSet.connect(scrollView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            constraintSet.connect(scrollView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            constraintSet.connect(scrollView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            constraintSet.applyTo(baseContainer)
            activity.layoutInflater.inflate(layoutConfig.contentLayoutId!!, scrollView)
        }
    }

    private fun createViewPager(): ViewPager {
        val viewPager = ViewPager(activity)
        viewPager.id = View.generateViewId()
        viewPager.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
        return viewPager
    }

    private fun createTabLayout(): TabLayout {
        val tabLayout = TabLayout(activity)
        tabLayout.id = View.generateViewId()
        tabLayout.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        return tabLayout
    }

    private fun setupTabLayout(tabLayout: TabLayout, viewPager: ViewPager) {
        val fragments = arrayListOf<FragmentBase>()
        for (viewPagerElement in layoutConfig.viewPagerElements) {
            val fragment = viewPagerElement.fragmentFactory.invoke()
            if (fragment is HistoryFragment<*> && viewPagerElement is ActivityOptions.ViewPagerHistoryElement<*>) {
                @Suppress("UNCHECKED_CAST")
                val historyFragment = fragment as HistoryFragment<PersistentObject>
                @Suppress("UNCHECKED_CAST")
                val historyElement = viewPagerElement as ActivityOptions.ViewPagerHistoryElement<PersistentObject>
                historyFragment.setup(historyElement.classType, historyElement.dao, historyElement.components, historyElement.liveDataGetter.invoke(), historyElement.groupingSelector)
            }
            fragments.add(fragment)
        }
        val adapter = TabsPagerFragmentAdapter(activity.supportFragmentManager, layoutConfig.viewPagerElements.count()) { position -> fragments[position] }
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) { }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                for (i in 0 until adapter.count) {
                    if (adapter.getItem(i) is FragmentBase) {
                        (adapter.getItem(i) as FragmentBase).onVisibilityChangedInViewPager(i == position)
                    }
                }
            }
        })
        tabLayout.setupWithViewPager(viewPager)
        val iconTintColor = (activity.application as ApplicationBase).themeManager.getColor(ThemeManager.TAB_LAYOUT_ICON_TINT)
        for (index in layoutConfig.viewPagerElements.indices) {
            val tab = tabLayout.getTabAt(index)
            tab?.setIcon(layoutConfig.viewPagerElements[index].iconResourceId)
            tab?.icon?.setColorFilter(iconTintColor, PorterDuff.Mode.SRC_IN)
        }
    }

    private fun createScrollView(): ScrollView {
        val scrollView = ScrollView(activity)
        scrollView.id = View.generateViewId()
        scrollView.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
        return scrollView
    }
}