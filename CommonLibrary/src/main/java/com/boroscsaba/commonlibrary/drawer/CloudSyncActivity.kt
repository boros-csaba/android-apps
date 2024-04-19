package com.boroscsaba.commonlibrary.drawer

import android.os.Bundle
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.activities.ActivityBase
import com.boroscsaba.commonlibrary.cloudsync.CloudSyncLoginFragment
import com.boroscsaba.commonlibrary.cloudsync.CloudSyncLogoutFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_cloud_sync.*


class CloudSyncActivity : ActivityBase() {

    init {
        options.layout = R.layout.activity_cloud_sync
        options.toolbarId = R.id.toolbar
        options.canShowAdConsentPopup = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.title = getString(R.string.navigation_drawer_cloud_sync)

        LoggingHelper.logEvent(this, "CloudSync_page")
        if (FirebaseAuth.getInstance().currentUser == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, CloudSyncLoginFragment())
                    .commitAllowingStateLoss()
        }
        else {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, CloudSyncLogoutFragment())
                    .commitAllowingStateLoss()
        }

        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            if (auth.currentUser == null) {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, CloudSyncLoginFragment())
                        .commitAllowingStateLoss()
            }
            else {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, CloudSyncLogoutFragment())
                        .commitAllowingStateLoss()
            }
        }
    }
}