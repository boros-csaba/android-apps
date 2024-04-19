package com.boroscsaba.commonlibrary.activities.helpers

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * Created by Boros Csaba
 */

class TabsPagerFragmentAdapter(manager: FragmentManager, private val count: Int, private val fragmentSelector: (position: Int) -> Fragment) : FragmentPagerAdapter(manager) {

    override fun getItem(position: Int): Fragment {
        return fragmentSelector(position)
    }

    override fun getCount(): Int {
        return count
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return null
    }
}
