package com.boroscsaba.myapplication.technical

import com.boroscsaba.myapplication.view.CompletedGoalsView
import com.boroscsaba.myapplication.view.OverviewFragment

/**
 * Created by Boros Csaba
 */

class OverviewTabsPagerFragmentAdapter(manager: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(manager) {

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        if (position == 0)
            return OverviewFragment()
        return CompletedGoalsView()
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return null
    }
}
