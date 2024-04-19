package com.boroscsaba.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.cloudsync.CloudSyncServiceBase
import com.boroscsaba.commonlibrary.drawer.CloudSyncActivity
import com.boroscsaba.commonlibrary.drawer.DrawerButton
import com.boroscsaba.commonlibrary.settings.Setting
import com.boroscsaba.commonlibrary.settings.SettingOption
import com.boroscsaba.commonlibrary.tutorial.TutorialLogItem
import com.boroscsaba.commonlibrary.widget.WidgetDataObserver
import com.boroscsaba.dataaccess.RepositoryBaseBase
import com.boroscsaba.myapplication.activities.PercentageWidget
import com.boroscsaba.myapplication.logic.PercentageDisplayType
import com.boroscsaba.myapplication.dataAccess.SettingOptionRepository
import com.boroscsaba.myapplication.dataAccess.TutorialLogItemRepository
import com.boroscsaba.myapplication.view.GoalsListView
import com.boroscsaba.myapplication.view.OverviewView
import com.boroscsaba.myapplication.dataAccess.CloudSyncService
import com.boroscsaba.myapplication.technical.NotificationPublisher



/**
* Created by boros on 3/18/2018.
*/
class MyPiggyBankTracker: ApplicationBase() {
    override val mainActivity = GoalsListView::class.java
    override var appVersion: String = ""
    override val premiumFeatureImageResources = arrayOf(R.drawable.unlimited_goals)
    override val premiumFeatureTitleResources = arrayOf(R.string.premium_feature_unlimited_goals_title)
    override val premiumFeatureDescriptionResources = arrayOf(R.string.premium_feature_unlimited_goals_description)
    override val splashScreenIcon: Int = R.mipmap.ic_launcher
    override val themeManager = ThemeManager()
    override val thankYouAdId: String = "ca-app-pub-7535188687645300/2176679078"
    override fun getTutorialLogItemRepository(): RepositoryBaseBase<TutorialLogItem> { return TutorialLogItemRepository(this) }
    override fun getSettingOptionRepository(): RepositoryBaseBase<SettingOption> { return SettingOptionRepository(this) }
    override fun getCloudSyncService(): CloudSyncServiceBase { return CloudSyncService(this) }
    override fun getDefaultWebClientId(): String { return getString(R.string.default_web_client_id) }
    override val notificationPublisher: Class<*>? = NotificationPublisher::class.java

    override fun onCreate() {
        super.onCreate()
        appVersion = getString(R.string.app_version)
    }

    override val startupSetup = {
        val thread = HandlerThread("MyWidgetHandlerThread")
        thread.start()
        val handler = Handler(thread.looper)
        val observer = WidgetDataObserver(this, handler, PercentageWidget::class.java)
        contentResolver.registerContentObserver(Uri.parse("content://${BuildConfig.CONTENT_AUTHORITY}/Goals"), true, observer)
    }

    override fun initializeDrawerButtons() {
        super.initializeDrawerButtons()

        drawerButtons.add(DrawerButton(com.boroscsaba.commonlibrary.R.string.statistics, R.drawable.ic_insert_chart_white_48dp, 2, 1) { activity ->
            val intent = Intent(this, OverviewView::class.java)
            activity.startActivity(intent)
        })
        drawerButtons.add(DrawerButton(R.string.completed_goals_menu, R.drawable.ic_check_white_48dp, 2, 2) { activity ->
            val intent = Intent(this, OverviewView::class.java)
            intent.putExtra("TabIndex", 1)
            activity.startActivity(intent)
        })

        drawerButtons.add(DrawerButton(R.string.navigation_drawer_cloud_sync, com.boroscsaba.commonlibrary.R.drawable.ic_sync_black_48dp, 10, 1) { activity ->
            val intent = Intent(this, CloudSyncActivity::class.java)
            activity.startActivity(intent)
        })
    }

    override fun initializeSettings() {
        super.initializeSettings()

        val percentageDisplaySetting = Setting(this, true, "PERCENTAGE_DISPLAY_SETTINGS", PercentageDisplayType.Percentage.value, getString(R.string.percentage_display_percentage), R.string.percentage_display_title, R.string.percentage_display_description)
        percentageDisplaySetting.setOption1(getString(R.string.percentage_display_percentage), PercentageDisplayType.Percentage.value)
        percentageDisplaySetting.setOption2(getString(R.string.percentage_display_collected), PercentageDisplayType.Collected.value)
        percentageDisplaySetting.setOption3(getString(R.string.percentage_display_left), PercentageDisplayType.Left.value)
        settingsConfigurations.add(percentageDisplaySetting)
    }
}