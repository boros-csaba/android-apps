package com.boroscsaba.englishirregularverbsmemorizer

import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.cloudsync.CloudSyncServiceBase
import com.boroscsaba.commonlibrary.settings.Setting
import com.boroscsaba.commonlibrary.settings.SettingOption
import com.boroscsaba.commonlibrary.tutorial.TutorialLogItem
import com.boroscsaba.dataaccess.RepositoryBaseBase
import com.boroscsaba.englishirregularverbsmemorizer.view.MainActivity
import com.boroscsaba.englishirregularverbsmemorizer.dataAccess.SettingOptionRepository
import com.boroscsaba.englishirregularverbsmemorizer.dataAccess.TutorialLogItemRepository


/**
 * Created by boros on 3/18/2018.
 */
class EnglishIrregularVerbsMemorizer: ApplicationBase() {
    override val mainActivity = MainActivity::class.java
    override var appVersion: String = ""
    override val premiumFeatureImageResources = emptyArray<Int>()
    override val premiumFeatureTitleResources = emptyArray<Int>()
    override val premiumFeatureDescriptionResources = emptyArray<Int>()
    override val splashScreenIcon: Int = R.mipmap.ic_launcher
    override val themeManager = ThemeManager()
    override val thankYouAdId: String = ""
    override fun getTutorialLogItemRepository(): RepositoryBaseBase<TutorialLogItem> { return TutorialLogItemRepository(this) }
    override fun getSettingOptionRepository(): RepositoryBaseBase<SettingOption> { return SettingOptionRepository(this) }
    override fun getCloudSyncService(): CloudSyncServiceBase? { return null }
    override fun getDefaultWebClientId(): String { return "189282740729-t4ap4cv6gdf9qv8fmnv6brmpco8j7acn.apps.googleusercontent.com" }

    init {
    }

    override fun onCreate() {
        super.onCreate()
        appVersion = getString(R.string.app_version)
    }

    override fun initializeSettings() {
        super.initializeSettings()

        val dailyGoalSetting = Setting(this, true, "DAILY_GOAL", 10.0, "10/${getString(R.string.day)}", R.string.daily_goal, R.string.daily_goal_description)
        dailyGoalSetting.showValueEditor(1, 1000, "/${getString(R.string.day)}")
        settingsConfigurations.add(dailyGoalSetting)
    }

    override val startupSetup = {
    }
}