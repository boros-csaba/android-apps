package com.boroscsaba.commonlibrary.settings

import android.app.Application
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.viewelements.currency.Currency
import com.boroscsaba.commonlibrary.viewelements.currency.CurrencyManager
import java.text.SimpleDateFormat
import java.util.*

class SettingsHelper(val application: Application) {

    fun getDisplayValue(settingName: String): String?  {
        val app = application as ApplicationBase
        return app.settings.firstOrNull { s -> s.settingName == settingName }?.displayValue
    }

    fun getValue(settingName: String): Double? {
        val app = application as ApplicationBase
        return app.settings.firstOrNull { s -> s.settingName == settingName }?.value
    }

    fun saveSetting(settingName: String, value: Double, displayValue: String) {
        val app = application as ApplicationBase
        app.settings.first { s -> s.settingName == settingName }.value = value
        app.settings.first { s -> s.settingName == settingName }.displayValue = displayValue

        val settingOption = app.getSettingOptionRepository().getObjects("setting_name = ?", arrayOf(settingName), null).first()
        settingOption.value = value
        settingOption.displayValue = displayValue
        app.getSettingOptionRepository().upsert(settingOption, false)
    }

    fun getAdsSetting(): AdsSettings? {
        return AdsSettings.values().firstOrNull { s -> s.value == getValue(ADS_SETTINGS) }
    }

    fun getDateFormat(): SimpleDateFormat {
        val setting = getDateFormatSetting()
        if (setting == DateFormatSettings.MDY) {
            return SimpleDateFormat("MM/dd/yyyy", Locale.US)
        }
        else if (setting == DateFormatSettings.DMY) {
            return SimpleDateFormat("dd/MM/yyyy", Locale.US)
        }
        return SimpleDateFormat("yyyy/MM/dd", Locale.US)
    }

    fun getCurrency(): Currency {
        val id = getValue("CURRENCY_SETTING")?.toInt() ?: 0
        return CurrencyManager.getCurrency(id)
    }

    fun isCloudSyncEnabled(): Boolean {
        val setting = getValue(CLOUD_SYNC_SETTINGS) ?: 0.0
        val isEnabled = setting > 0.1
        val applicationBase = application as ApplicationBase
        val drawerButton = applicationBase.drawerButtons.firstOrNull{ b -> b.titleResourceId == R.string.navigation_drawer_cloud_sync }
        if (drawerButton != null) {
            //todo change icon
        }

        return isEnabled
    }

    fun setCloudSyncState(enabled: Boolean) {
        val app = application as ApplicationBase
        app.settings.first { s -> s.settingName == CLOUD_SYNC_SETTINGS }.value = if (enabled) 1.0 else 0.0
        val settingOption = app.getSettingOptionRepository().getObjects("setting_name = ?", arrayOf(CLOUD_SYNC_SETTINGS), null).first()
        settingOption.value = if (enabled) 1.0 else 0.0
        app.getSettingOptionRepository().upsert(settingOption, true)
        if (enabled) {
            app.getCloudSyncService()?.synchronizeData()
        }
    }

    private fun getDateFormatSetting(): DateFormatSettings? {
        return DateFormatSettings.values().firstOrNull { s -> s.value == getValue(DATE_FORMAT_SETTINGS) }
    }

    companion object {
        const val ADS_SETTINGS = "ADS_SETTINGS"
        const val DATE_FORMAT_SETTINGS = "DATE_FORMAT_SETTINGS"
        const val CLOUD_SYNC_SETTINGS =  "CLOUD_SYNC_SETTINGS"
        const val NOTIFICATION_SETTINGS = "NOTIFICATION_SETTINGS"
    }
}