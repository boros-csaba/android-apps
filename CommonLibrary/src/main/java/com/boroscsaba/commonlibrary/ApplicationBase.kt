package com.boroscsaba.commonlibrary

import android.app.Application
import android.content.Intent
import android.os.StrictMode
import androidx.appcompat.app.AppCompatDelegate
import com.boroscsaba.commonlibrary.activities.helpers.OnBoardingPage
import com.boroscsaba.commonlibrary.backup.BackupActivity
import com.boroscsaba.commonlibrary.cloudsync.CloudSyncServiceBase
import com.boroscsaba.commonlibrary.drawer.*
import com.boroscsaba.commonlibrary.helpers.AppRatingHelper
import com.boroscsaba.commonlibrary.helpers.PremiumHelper
import com.boroscsaba.commonlibrary.settings.*
import com.boroscsaba.commonlibrary.tutorial.TutorialLogItem
import com.boroscsaba.dataaccess.RepositoryBaseBase
import com.google.firebase.firestore.ListenerRegistration
import com.boroscsaba.commonlibrary.helpers.OtherAppsHelper
import com.boroscsaba.commonlibrary.viewelements.SimplePopupWithOptions
import com.facebook.stetho.Stetho
import com.facebook.stetho.Stetho.newInitializerBuilder
import com.squareup.leakcanary.LeakCanary
import java.util.concurrent.CopyOnWriteArrayList


/**
* Created by boros on 3/18/2018.
*/
abstract class ApplicationBase: Application() {
    var initialized: Int = 0
    abstract val splashScreenIcon: Int
    abstract val mainActivity: Class<*>

    //todo remove
    var appStartLog = ""

    abstract var appVersion: String
    abstract val startupSetup: () -> Unit
    abstract val premiumFeatureImageResources: Array<Int>
    abstract val premiumFeatureTitleResources: Array<Int>
    abstract val premiumFeatureDescriptionResources: Array<Int>
    abstract val themeManager: ThemeManager
    abstract val thankYouAdId: String
    abstract fun getDefaultWebClientId(): String
    abstract fun getTutorialLogItemRepository(): RepositoryBaseBase<TutorialLogItem>
    abstract fun getSettingOptionRepository(): RepositoryBaseBase<SettingOption>
    abstract fun getCloudSyncService(): CloudSyncServiceBase?
    val settings = CopyOnWriteArrayList<SettingOption>()
    val settingsConfigurations = ArrayList<Setting>()
    val drawerButtons = ArrayList<DrawerButton>()
    var cloudChangeListener: ListenerRegistration? = null
    var showOtherAppsPremiumWonPopup = false
    var showOtherAppsPremiumLostPopup = false
    open val notificationPublisher: Class<*>? = null
    val onBoardingPages = arrayListOf<OnBoardingPage>()

    override fun onCreate() {
        super.onCreate()

        appStartLog += "AppStart"
        themeManager.initSetting(this)
        if (ThemeManager.isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        if (BuildConfig.DEBUG) {
            Stetho.initialize(newInitializerBuilder(this)
                    .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                    .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                    .build())
            LeakCanary.install(this)
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                    .detectActivityLeaks()
                    .detectLeakedClosableObjects()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedRegistrationObjects()
                    .detectFileUriExposure()
                    .penaltyLog()
                    .build())
        }
    }

    open fun initializeSettings() {
        settingsConfigurations.clear()
        val adsSetting = Setting(this, true, SettingsHelper.ADS_SETTINGS, AdsSettings.NO_ADS.value, getString(R.string.settings_no_ads), R.string.settings_ads, R.string.settings_description)
        adsSetting.setOption1(getString(R.string.settings_personalized_ads), AdsSettings.PERSONALIZED.value)
        adsSetting.setOption2(getString(R.string.settings_non_personalized_ads), AdsSettings.NON_PERSONALIZED.value)
        adsSetting.setOption3(getString(R.string.settings_no_ads), AdsSettings.NO_ADS.value)

        adsSetting.option3?.action = { activity ->
            if (PremiumHelper.canShowAds()) {
                val dialog = SimplePopupWithOptions(activity)
                dialog.setCancelable(false)
                dialog.setTitle(R.string.navigation_drawer_premium)
                dialog.setMessage(getString(R.string.no_free_version_without_ads))
                dialog.setOption1(R.string.navigation_drawer_premium) {
                    val intent = Intent(this, GoPremiumActivity::class.java)
                    activity.startActivity(intent)
                }
                dialog.setOption2(R.string.settings_ads) {
                    val adSetting = settingsConfigurations.first { s -> s.name == SettingsHelper.ADS_SETTINGS }
                    LoggingHelper.logEvent(this, "Ad_consent_popup")
                    SettingsPopupHelper.showSettingPopup(adSetting, activity, false)
                }
                dialog.setOption3(R.string.close_app) {
                    LoggingHelper.logEvent(this, "Close_app_on_consent")
                    activity.finishAffinity()
                }
                dialog.show()
            }
        }
        settingsConfigurations.add(adsSetting)

        val dateFormatSetting = Setting(this, true, SettingsHelper.DATE_FORMAT_SETTINGS, DateFormatSettings.YMD.value, getString(R.string.date_format_ymd), R.string.date_format_title, R.string.date_format_description)
        dateFormatSetting.setOption1(getString(R.string.date_format_ymd), DateFormatSettings.YMD.value)
        dateFormatSetting.setOption2(getString(R.string.date_format_dmy), DateFormatSettings.DMY.value)
        dateFormatSetting.setOption3(getString(R.string.date_format_mdy), DateFormatSettings.MDY.value)
        settingsConfigurations.add(dateFormatSetting)

        val cloudSyncSetting = Setting(this, false, SettingsHelper.CLOUD_SYNC_SETTINGS, 0.0, "", R.string.navigation_drawer_cloud_sync, null)
        cloudSyncSetting.setOption1(getString(R.string.cloud_sync_enable), 1.0)
        cloudSyncSetting.setOption2(getString(R.string.cloud_sync_disable), 0.0)
        settingsConfigurations.add(cloudSyncSetting)

        settingsConfigurations.add(Setting(this, false, SettingsHelper.CLOUD_SYNC_SETTINGS, 0.0, "", R.string.navigation_drawer_cloud_sync, null))

        val notificationSettings = Setting(this, true, SettingsHelper.NOTIFICATION_SETTINGS, 1.0, getString(R.string.notifications_enable), R.string.notification_settings_title, R.string.notification_settings_description)
        notificationSettings.setOption1(getString(R.string.notifications_enable), 1.0)
        notificationSettings.setOption2(getString(R.string.notifications_disable), 0.0)
        settingsConfigurations.add(notificationSettings)
    }

    open fun initializeDrawerButtons() {
        drawerButtons.clear()
        drawerButtons.add(DrawerButton(R.string.navigation_drawer_premium, R.drawable.ic_shopping_cart_black_48dp, 10, 1) { activity ->
            Utils.openActivity(activity, GoPremiumActivity::class.java)
        })
        drawerButtons.add(DrawerButton(R.string.navigation_drawer_feedback, R.drawable.ic_feedback_black_48dp, 10, 2) { activity ->
            Utils.openActivity(activity, SendFeedbackActivity::class.java)
        })
        drawerButtons.add(DrawerButton(R.string.navigation_drawer_rate_app, R.drawable.ic_star_black_48dp, 10, 3) { activity ->
            LoggingHelper.logEvent(this,"App_rating")
            AppRatingHelper.showRateAppPopup(activity)
        })

        drawerButtons.add(DrawerButton(R.string.dark_mode, R.drawable.ic_moon, 10, 1) { activity ->
            if (PremiumHelper.isPremium) {
                LoggingHelper.logEvent(activity, "DarkModeToggled")
                val isDarkMode = ThemeManager.isDarkMode
                ThemeManager.isDarkMode = !isDarkMode
                if (isDarkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                themeManager.saveSetting(!isDarkMode, this)
                activity.recreate()
            }
            else {
                LoggingHelper.logEvent(activity, "DarkModeNoPremium")
                Utils.openActivity(activity, GoPremiumActivity::class.java)
            }
        })
        drawerButtons.add(DrawerButton(R.string.navigation_drawer_backup, R.drawable.ic_settings_backup_restore_black_48dp, 20, 2) { activity ->
            Utils.openActivity(activity, BackupActivity::class.java)
        })
        drawerButtons.add(DrawerButton(R.string.navigation_drawer_settings, R.drawable.ic_settings_black_48dp, 20, 3) { activity ->
            Utils.openActivity(activity, SettingsActivity::class.java)
        })
        drawerButtons.add(DrawerButton(R.string.navigation_drawer_data_policy, R.drawable.ic_assignment_black_48dp, 20, 4) { activity ->
            Utils.openActivity(activity, DataPolicyActivity::class.java)
        })
        drawerButtons.add(DrawerButton(R.string.navigation_drawer_other_apps, R.drawable.ic_apps_black_48dp, 20, 5) { activity ->
            Utils.openActivity(activity, OtherAppsActivity::class.java)
        })
    }

    fun initializeAppsList() {
        OtherAppsHelper(this).checkApps()
    }
}