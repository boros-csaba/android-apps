package com.boroscsaba.bodymeasurementsandweightlosstracker

import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.cloudsync.CloudSyncServiceBase
import com.boroscsaba.commonlibrary.settings.SettingOption
import com.boroscsaba.commonlibrary.tutorial.TutorialLogItem
import com.boroscsaba.dataaccess.RepositoryBaseBase
import com.boroscsaba.bodymeasurementsandweightlosstracker.dataAccess.SettingOptionRepository
import com.boroscsaba.bodymeasurementsandweightlosstracker.dataAccess.TutorialLogItemRepository
import com.boroscsaba.bodymeasurementsandweightlosstracker.model.AppDatabase


/**
 * Created by boros on 3/18/2018.
 */
class BodyMeasurementsAndWeightLossTracker: ApplicationBase() {
    override val mainActivity = MainActivity::class.java
    override var appVersion: String = ""
    override val premiumFeatureImageResources = emptyArray<Int>()
    override val premiumFeatureTitleResources = emptyArray<Int>()
    override val premiumFeatureDescriptionResources = emptyArray<Int>()
    override val splashScreenIcon: Int = R.mipmap.ic_launcher
    override val themeManager = ThemeManager()
    override val thankYouAdId: String = "ca-app-pub-7535188687645300/6788016819"
    override fun getTutorialLogItemRepository(): RepositoryBaseBase<TutorialLogItem> { return TutorialLogItemRepository(this) }
    override fun getSettingOptionRepository(): RepositoryBaseBase<SettingOption> { return SettingOptionRepository(this) }
    override fun getCloudSyncService(): CloudSyncServiceBase? { return null }
    override fun getDefaultWebClientId(): String { return "710494740846-aea3ktab9sa6f8mbjro7772bkp9te4o6.apps.googleusercontent.com" } //todo

    override fun onCreate() {
        super.onCreate()
        appVersion = getString(R.string.app_version)

        AppDatabase.getDatabase(this)
    }

    override val startupSetup = {
    }
}