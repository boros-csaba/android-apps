package com.boroscsaba.fastinglifestyletimerandtracker

import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.activities.helpers.OnBoardingPage
import com.boroscsaba.commonlibrary.cloudsync.CloudSyncServiceBase
import com.boroscsaba.commonlibrary.settings.SettingOption
import com.boroscsaba.commonlibrary.tutorial.TutorialLogItem
import com.boroscsaba.dataaccess.RepositoryBaseBase
import com.boroscsaba.fastinglifestyletimerandtracker.activities.MainActivity
import com.boroscsaba.fastinglifestyletimerandtracker.dataAccess.SettingOptionRepository
import com.boroscsaba.fastinglifestyletimerandtracker.dataAccess.TutorialLogItemRepository


/**
* Created by boros on 3/18/2018.
*/
class FastingLifestyleTimerAndTracker: ApplicationBase() {
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
    override fun getDefaultWebClientId(): String { return "710494740846-aea3ktab9sa6f8mbjro7772bkp9te4o6.apps.googleusercontent.com" }

    init {
        val trackEatingWindowsPage = OnBoardingPage()
        trackEatingWindowsPage.titleResourceId = R.string.on_boarding_tracker_title
        trackEatingWindowsPage.imageResourceId = R.drawable.ic_onboarding_track_eating_window
        trackEatingWindowsPage.descriptionResourceId = R.string.on_boarding_tracker_desc
        trackEatingWindowsPage.backgroundColor = "#ED6F60"
        trackEatingWindowsPage.textColor = "#ffffff"
        onBoardingPages.add(trackEatingWindowsPage)

        val checkProgressPage = OnBoardingPage()
        checkProgressPage.titleResourceId = R.string.on_boarding_progress_title
        checkProgressPage.imageResourceId = R.drawable.ic_onboarding_chart_line
        checkProgressPage.descriptionResourceId = R.string.on_boarding_progress_desc
        checkProgressPage.backgroundColor = "#ED6F60"
        checkProgressPage.textColor = "#ffffff"
        onBoardingPages.add(checkProgressPage)

        val disclaimerPage = OnBoardingPage()
        disclaimerPage.titleResourceId = R.string.on_boarding_disclaimer_title
        disclaimerPage.descriptionResourceId = R.string.on_boarding_disclaimer_desc
        disclaimerPage.backgroundColor = "#ED6F60"
        disclaimerPage.textColor = "#ffffff"
        onBoardingPages.add(disclaimerPage)
    }

    override fun onCreate() {
        super.onCreate()
        appVersion = getString(R.string.app_version)
    }

    override val startupSetup = {
    }
}