package com.boroscsaba.commonlibrary.splashscreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.AsyncTask
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.commonlibrary.activities.OnBoarding
import com.boroscsaba.commonlibrary.helpers.NotificationHelper
import com.boroscsaba.commonlibrary.helpers.PremiumHelper
import com.boroscsaba.commonlibrary.settings.SettingOption
import com.boroscsaba.commonlibrary.settings.SettingsHelper
import com.boroscsaba.commonlibrary.tutorial.TutorialHelper
import com.boroscsaba.commonlibrary.tutorial.TutorialsEnum


class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = (application as ApplicationBase)
        if (savedInstanceState != null) {
            app.appStartLog += " XClosed"
            return
        }

        app.appStartLog += " SplashScreen "

        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()

        val view = SplashScreenView(this)
        setContentView(view)

        app.initialized++
        if (app.initialized > 1) {
            LoggingHelper.logException(Exception("App initialized ${app.initialized} times: ${app.appStartLog}!"), this)
        }
        val closeAfterInit = intent.getBooleanExtra("CLOSE_AFTER_INIT", false)

        AsyncTask().execute({
            app.appStartLog += " SplashScreenAsync "
            val fabricHelper = LoggingHelper()
            fabricHelper.initialize(this)

            PremiumHelper.getInstance(app).initIsPremiumFlagFromSharedPreferences()
            app.initializeAppsList()
            app.initializeSettings()
            app.initializeDrawerButtons()
            app.startupSetup.invoke()
            addNewSettingsToDatabase(app)

            startApplication(app, closeAfterInit)

            if (app.notificationPublisher != null) {
                NotificationHelper.scheduleNotifications(app, app.notificationPublisher!!)
            }
            com.boroscsaba.dataaccess.AsyncTask().execute({
                app.getCloudSyncService()?.synchronizeData()
            })
            PremiumHelper.getInstance(app).refreshIsPremiumFlagAsync()
        })
    }

    private fun startApplication(app: ApplicationBase, closeAfterInit: Boolean) {
        if (!closeAfterInit) {
            val tutorialHelper = TutorialHelper(app)
            if (app.onBoardingPages.isNotEmpty() && !tutorialHelper.isTutorialCompleted(TutorialsEnum.ON_BOARDING)) {
                val intent = Intent(app, OnBoarding::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                app.startActivity(intent)
                app.appStartLog += " PremiumHelper "
            }
            else {
                val intent = Intent(app, app.mainActivity)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                app.startActivity(intent)
                app.appStartLog += " PremiumHelper "
            }
        }
        finish()
    }

    private fun addNewSettingsToDatabase(app: ApplicationBase) {
        var settings = app.getSettingOptionRepository().getObjects(null, null, null)
        app.settingsConfigurations.forEach { sc ->
            if (!settings.any { s -> s.settingName == sc.name }) {
                val newSettingOption = SettingOption(this)
                newSettingOption.settingName = sc.name
                newSettingOption.value = sc.defaultValue
                newSettingOption.displayValue = sc.defaultValueDisplay
                app.getSettingOptionRepository().upsert(newSettingOption, false)
            }
        }
        settings = app.getSettingOptionRepository().getObjects(null, null, null)
        val notificationSetting = settings.firstOrNull { s -> s.settingName == SettingsHelper.NOTIFICATION_SETTINGS }
        if (notificationSetting != null) {
            val newValue = notificationSetting.value > 0.1
            val sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE)
            if (sharedPreferences.getBoolean(SettingsHelper.NOTIFICATION_SETTINGS, false) != newValue) {
                val editor = sharedPreferences.edit()
                editor.putBoolean(SettingsHelper.NOTIFICATION_SETTINGS, newValue)
                editor.apply()
            }
        }
        Handler(Looper.getMainLooper()).post{ app.settings.addAll(settings) }
    }
}
