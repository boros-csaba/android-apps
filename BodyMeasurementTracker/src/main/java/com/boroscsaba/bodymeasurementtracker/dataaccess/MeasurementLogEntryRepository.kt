package com.boroscsaba.bodymeasurementtracker.dataaccess

import android.content.Context
import com.boroscsaba.bodymeasurementtracker.model.MeasurementLogEntry
import com.boroscsaba.commonlibrary.LoggingHelper

/**
 * Created by Boros Csaba
 */
class MeasurementLogEntryRepository(val context: Context) : RepositoryBase<MeasurementLogEntry>(context, MeasurementLogEntry::class.java, MeasurementLogEntryMapper(context)) {

    override fun upsert(persistentObject: MeasurementLogEntry, alreadySynced: Boolean) : Int {
        if (persistentObject.id == 0) {
            LoggingHelper.logEvent(context, "New_Measurement_Log_Entry")
        }
        return super.upsert(persistentObject, alreadySynced)
    }
}