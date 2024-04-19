package com.boroscsaba.commonlibrary.activities

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.drawerlayout.widget.DrawerLayout
import com.boroscsaba.commonlibrary.*
import com.boroscsaba.commonlibrary.activities.helpers.AdHelper
import com.boroscsaba.commonlibrary.helpers.PremiumHelper
import com.boroscsaba.commonlibrary.views.ImageView
import com.boroscsaba.commonlibrary.views.NavigationView
import kotlinx.android.synthetic.main.base_layout_drawer.*

/**
* Created by boros on 3/16/2018.
*/
abstract class ActivityDrawerBase(private val drawerLayout: Int, private val launcherIcon: Int): ActivityBase(), com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener {

    private var drawerToggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupDrawer()
        val playAdButton = findViewById<com.boroscsaba.commonlibrary.views.ImageButton>(R.id.playAdButton)
        if (playAdButton != null) {
            AdHelper(this).setupPlayAdButton(playAdButton)
        }
    }

    private fun setupDrawer() {
        val drawer = findViewById<DrawerLayout>(drawerLayout)
        drawerToggle = object : ActionBarDrawerToggle(this, drawer, R.string.yes, R.string.no) {
            override fun onDrawerOpened(drawerView: View) {
                invalidateOptionsMenu()
            }
            override fun onDrawerClosed(view: View) {
                invalidateOptionsMenu()
            }
        }
        val drawerToggle = drawerToggle ?: return
        drawerToggle.isDrawerIndicatorEnabled = true
        drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        val navigationView = drawer.getChildAt(drawer.childCount - 1) as NavigationView
        navigationView.setIcon(launcherIcon)
        navigationView.addButtons((application as ApplicationBase).drawerButtons, this, drawer)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return drawerToggle!!.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return false
    }
}