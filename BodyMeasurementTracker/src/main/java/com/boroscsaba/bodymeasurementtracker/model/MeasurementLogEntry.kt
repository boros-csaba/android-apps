package com.boroscsaba.bodymeasurementtracker.model

import android.content.Context
import com.boroscsaba.bodymeasurementtracker.dataaccess.MeasurementLogEntryRepository
import com.boroscsaba.dataaccess.IObjectWithId
import com.boroscsaba.dataaccess.PersistentObject
import com.boroscsaba.dataaccess.RepositoryBaseBase

/**
 * Created by Boros Csaba
 */
class MeasurementLogEntry(override val context: Context) : PersistentObject(), IObjectWithId {
    override var id: Int = 0
    override var createdDate: Long = 0
    override var modifiedDate: Long = 0
    override var guid = ""
    var parameterId: Int = 0
    var value : Double = 0.0
    var logDate: Long = 0

    var parameter: Parameter? = null

    override fun getRepository(): RepositoryBaseBase<PersistentObject>? {
        @Suppress("UNCHECKED_CAST")
        return MeasurementLogEntryRepository(context) as RepositoryBaseBase<PersistentObject>
    }
}