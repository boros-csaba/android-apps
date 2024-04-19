package com.boroscsaba.bodymeasurementtracker.dataaccess

import android.content.Context

import com.boroscsaba.bodymeasurementtracker.model.Parameter
import com.boroscsaba.bodymeasurementtracker.model.MeasurementLogEntry
import com.boroscsaba.commonlibrary.settings.SettingOptionDbPatch
import com.boroscsaba.commonlibrary.tutorial.TutorialDbPatch
import com.boroscsaba.commonlibrary.tutorial.TutorialLogItem
import com.boroscsaba.commonlibrary.tutorial.TutorialLogItemMapper
import com.boroscsaba.dataaccess.*

/**
 * Created by Boros Csaba
 */

internal object DataAccessProvider: DataAccessProviderBase() {

    private const val DATABASE_NAME = "MeasurementDB"

    override fun getDataAccessInternal(context: Context): DataAccess {
        val dbPatches : Array<IDbPatch> = arrayOf(
                InitialDbPatch(),
                TutorialDbPatch(),
                SettingOptionDbPatch(),
                EmptyLogItemDbPatch(),
                ParameterDashboardSettingsDbPatch(),
                RemoveMeasurementLogDbPatch(),
                TargetValueAndBmiDbPatch(),
                TargetDueDateDbPatch())
        val dataAccess = DataAccess(context, DATABASE_NAME, dbPatches)

        @Suppress("UNCHECKED_CAST")
        dataAccess.registerDataMapper(TutorialLogItem::class.java, TutorialLogItemMapper(context) as DataMapper<PersistentObject>)
        @Suppress("UNCHECKED_CAST")
        dataAccess.registerDataMapper(Parameter::class.java, ParameterMapper(context) as DataMapper<PersistentObject>)
        @Suppress("UNCHECKED_CAST")
        dataAccess.registerDataMapper(MeasurementLogEntry::class.java, MeasurementLogEntryMapper(context) as DataMapper<PersistentObject>)

        return dataAccess
    }
}
