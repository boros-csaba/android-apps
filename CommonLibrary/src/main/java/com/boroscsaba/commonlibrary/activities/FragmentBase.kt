package com.boroscsaba.commonlibrary.activities

import android.os.Bundle
import android.view.*
import com.boroscsaba.commonlibrary.activities.helpers.AdHelper
import com.boroscsaba.commonlibrary.activities.helpers.ToolbarButton
import com.google.android.gms.ads.AdView

/**
 * Created by Boros Csaba
 */

abstract class FragmentBase(val layout: Int) : androidx.fragment.app.Fragment() {

    val options = FragmentOptions()
    private var adView: AdView? = null
    val buttons = ArrayList<ToolbarButton>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setListeners()

        val adOptions = options.adsOptions
        if (adOptions != null) {
            adView = AdHelper(this.activity as ActivityBase).addAdView(adOptions, false)
        }
    }

    open fun onVisibilityChangedInViewPager(visible: Boolean) { }

    override fun onPause() {
        super.onPause()
        adView?.pause()
    }

    override fun onResume() {
        super.onResume()
        adView?.resume()
    }

    open fun setListeners() {}

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        for (button in buttons) {
            if (button.isActive) {
                val menuItem = menu.add(button.titleResource)
                menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                if (button.iconResource != null) {
                    menuItem.setIcon(button.iconResource)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val button = buttons.firstOrNull{ b -> getString(b.titleResource) == item.title } ?: return super.onOptionsItemSelected(item)
        if (!button.executed)
        {
            button.executed = button.action?.invoke() ?: false
        }
        return true
    }
}
