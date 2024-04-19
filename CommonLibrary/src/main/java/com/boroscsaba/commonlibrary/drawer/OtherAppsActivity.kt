package com.boroscsaba.commonlibrary.drawer

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.activities.ActivityBase
import com.boroscsaba.commonlibrary.adapters.SimpleRecyclerViewAdapter
import com.boroscsaba.commonlibrary.helpers.OtherAppsHelper
import kotlinx.android.synthetic.main.activity_other_apps.*
import kotlinx.android.synthetic.main.other_app_row_layout.view.*

class OtherAppsActivity : ActivityBase() {

    init {
        options.layout = R.layout.activity_other_apps
        options.toolbarId = R.id.toolbar
        options.canShowAdConsentPopup = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.title = getString(R.string.navigation_drawer_other_apps)
        LoggingHelper.logEvent(this, "OtherAppsActivity")
        val helper = OtherAppsHelper(application as ApplicationBase)
        val apps = ArrayList(helper.getApps())
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        val adapter = SimpleRecyclerViewAdapter(apps, R.layout.other_app_row_layout, { holder, app ->
            holder.itemView.appTitle.text = app.title
            holder.itemView.appIcon.setImageResource(app.icon)
            if (app.installed) {
                holder.itemView.appActionButton.setImageResource(R.drawable.ic_check_white_48dp)
            }
            else {
                holder.itemView.appActionButton.setImageResource(R.drawable.ic_file_download_black_48dp)
            }
            holder.itemView.appActionButton.setColorFilter(Color.parseColor("#4caf50"))
            holder.itemView.otherAppButton.setOnClickListener {
                if (app.installed) {
                    val launchIntent = packageManager.getLaunchIntentForPackage(app.packageName)
                    if (launchIntent != null) {
                        startActivity(launchIntent)
                    }
                }
                else {
                    LoggingHelper.logEvent(this, "Go_to_app_" + app.packageName)
                    val uri = Uri.parse("market://details?id=" + app.packageName)
                    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                    try {
                        startActivity(goToMarket)
                    } catch (e: ActivityNotFoundException) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + app.packageName)))
                    }
                }
            }
        })
        recyclerView.adapter = adapter
    }
}
