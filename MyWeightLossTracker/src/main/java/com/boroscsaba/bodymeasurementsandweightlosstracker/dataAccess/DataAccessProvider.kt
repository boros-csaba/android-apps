package com.boroscsaba.myweightlosstracker.dataAccess

import android.content.Context

import com.boroscsaba.dataaccess.DataAccess

import com.boroscsaba.commonlibrary.settings.SettingOptionDbPatch
import com.boroscsaba.commonlibrary.tutorial.TutorialDbPatch
import com.boroscsaba.dataaccess.DataAccessProviderBase

/**
 * Created by Boros Csaba
 */

object DataAccessProvider: DataAccessProviderBase() {

    private const val DATABASE_NAME = "MeasurementsDB"

    override fun getDataAccessInternal(context: Context): DataAccess {
        val dbPatches = arrayOf(
                InitialDbPatch(),
                TutorialDbPatch(),
                SettingOptionDbPatch())
        return DataAccess(context, DATABASE_NAME, dbPatches)
    }
}
