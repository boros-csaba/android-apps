package com.boroscsaba.bodymeasurementtracker

import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import com.boroscsaba.bodymeasurementtracker.activities.MainActivity
import com.boroscsaba.bodymeasurementtracker.dataaccess.SettingOptionRepository
import com.boroscsaba.bodymeasurementtracker.dataaccess.TutorialLogItemRepository
import com.boroscsaba.bodymeasurementtracker.technical.NotificationPublisher
import com.boroscsaba.bodymeasurementtracker.widget.Widget
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.cloudsync.CloudSyncServiceBase
import com.boroscsaba.commonlibrary.settings.Setting
import com.boroscsaba.commonlibrary.settings.SettingOption
import com.boroscsaba.commonlibrary.tutorial.TutorialLogItem
import com.boroscsaba.commonlibrary.widget.WidgetDataObserver
import com.boroscsaba.dataaccess.RepositoryBaseBase



class BodyMeasurementTracker: ApplicationBase() {
    override val mainActivity = MainActivity::class.java
    override var appVersion: String = ""
    override val premiumFeatureImageResources = emptyArray<Int>()
    override val premiumFeatureTitleResources = emptyArray<Int>()
    override val premiumFeatureDescriptionResources = emptyArray<Int>()
    override val themeManager = ThemeManager()
    override val thankYouAdId: String = ""
    override fun getTutorialLogItemRepository(): RepositoryBaseBase<TutorialLogItem> { return TutorialLogItemRepository(this) }
    override fun getSettingOptionRepository(): RepositoryBaseBase<SettingOption> { return SettingOptionRepository(this) }
    override val splashScreenIcon = R.mipmap.ic_launcher
    override fun getDefaultWebClientId(): String { return this.getString(R.string.default_web_client_id) }
    override fun getCloudSyncService(): CloudSyncServiceBase? { return null }
    override val notificationPublisher: Class<*>? = NotificationPublisher::class.java

    override fun onCreate() {
        super.onCreate()
        appVersion = getString(R.string.app_version)
    }

    override val startupSetup = {
        val thread = HandlerThread("MyWidgetHandlerThread")
        thread.start()
        val handler = Handler(thread.looper)
        val observer = WidgetDataObserver(this, handler, Widget::class.java)
        contentResolver.registerContentObserver(Uri.parse("content://${BuildConfig.CONTENT_AUTHORITY}/Parameters"), true, observer)
    }

    override fun initializeSettings() {
        super.initializeSettings()

        val sexSetting = Setting(this, true, "SEX_SETTINGS", 1.0, "Male", R.string.sex, R.string.sex_setting_description)
        sexSetting.setOption1("Male", 1.0)
        sexSetting.setOption2("Female", 0.0)
        settingsConfigurations.add(sexSetting)
    }
}