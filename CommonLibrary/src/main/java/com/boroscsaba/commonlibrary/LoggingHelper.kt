package com.boroscsaba.commonlibrary

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import io.fabric.sdk.android.Fabric
import java.util.*

/**
 * Created by Boros Csaba
 */

class LoggingHelper {

    fun initialize(context: Context) {
        val androidId = getDeviceId(context)
        Fabric.with(context, Crashlytics())
        Crashlytics.setUserIdentifier(androidId)
    }

    fun getDeviceId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preferences_name), MODE_PRIVATE)
        var userGuid = sharedPreferences.getString("userGuid", null)
        if (userGuid == null) {
            userGuid = UUID.randomUUID().toString()
            with (sharedPreferences.edit()) {
                putString("userGuid", userGuid)
                apply()
            }
        }

        return userGuid
    }

    companion object {
        fun logEvent(context: Context, eventName: String, parameterName: String? = null, parameterValue: String? = null) {
            val params = Bundle()
            if (parameterName != null && parameterValue != null) {
                if (Utils.toDoubleOrNull(parameterValue) != null) {
                    params.putDouble(parameterName, Utils.toDoubleOrNull(parameterValue)!!)
                } else {
                    params.putString(parameterName, parameterValue)
                }
            }
            FirebaseAnalytics.getInstance(context).logEvent(eventName, params)
        }

        fun logException(exception: Exception, context: Context, extraInformation: String? = null): Exception? {
            return try {
                if (!Fabric.isInitialized()) {
                    LoggingHelper().initialize(context)
                }
                if (extraInformation != null) {
                    Crashlytics.setString("EXTRA", extraInformation)
                }
                Crashlytics.logException(exception)
                null
            } catch (e: Exception) {
                e
            }
        }
    }
}