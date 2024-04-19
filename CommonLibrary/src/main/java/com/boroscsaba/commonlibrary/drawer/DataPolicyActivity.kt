package com.boroscsaba.commonlibrary.drawer

import android.os.Bundle
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.activities.ActivityBase
import kotlinx.android.synthetic.main.activity_data_policy.*

class DataPolicyActivity : ActivityBase() {

    init {
        options.layout = R.layout.activity_data_policy
        options.toolbarId = R.id.toolbar
        options.canShowAdConsentPopup = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.title = getString(R.string.navigation_drawer_data_policy)

        LoggingHelper.logEvent(this, "DataPolicy")
        dataPolicyLink.text = getString(R.string.data_policy_link, "https://boroscsaba94.github.io/${packageName}_PrivacyPolicy.html")
    }
}
