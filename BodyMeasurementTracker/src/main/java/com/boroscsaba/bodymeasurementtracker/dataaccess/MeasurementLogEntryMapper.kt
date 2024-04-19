package com.boroscsaba.bodymeasurementtracker.dataaccess

import android.content.Context
import com.boroscsaba.bodymeasurementtracker.model.MeasurementLogEntry
import com.boroscsaba.dataaccess.DataMapper
import com.boroscsaba.dataaccess.Property
import com.boroscsaba.dataaccess.PropertyType
import java.util.ArrayList
import java.util.HashMap

/**
* Created by boros on 1/13/2018.
*/
class MeasurementLogEntryMapper(val application: Context) : DataMapper<MeasurementLogEntry>(application) {

    override val tableName = DATABASE_TABLE_NAME

    override fun getProperties(persistentObject: MeasurementLogEntry?): List<Property<Any>> {
        val properties = ArrayList<Property<Any>>()

        var id = 0
        var parameterId = 0
        var value: Number = 0
        var logDate: Long = 0
        var createdDate: Long = 0
        var modifiedDate: Long = 0

        if (persistentObject != null) {
            id = persistentObject.id
            parameterId = persistentObject.parameterId
            value = persistentObject.value
            logDate = persistentObject.logDate
            createdDate = persistentObject.createdDate
            modifiedDate = persistentObject.modifiedDate
        }

        properties.add(Property("id", id, PropertyType.Int))
        properties.add(Property("parameterId", parameterId, PropertyType.Int))
        properties.add(Property("entry_value", value, PropertyType.Double))
        properties.add(Property("log_date", logDate, PropertyType.DateTime))
        properties.add(Property("created_date", createdDate, PropertyType.DateTime))
        properties.add(Property("modified_date", modifiedDate, PropertyType.DateTime))

        return properties
    }

    override fun map(hashMap: HashMap<String, Any>): MeasurementLogEntry {
        return map(MeasurementLogEntry(application), hashMap)
    }

    override fun map(persistentObject: MeasurementLogEntry, hashMap: HashMap<String, Any>): MeasurementLogEntry {
        persistentObject.id = hashMap["id"] as Int
        persistentObject.parameterId = hashMap["parameterId"] as Int
        persistentObject.value = hashMap["entry_value"] as Double
        persistentObject.logDate = hashMap["log_date"] as Long
        persistentObject.createdDate = hashMap["created_date"] as Long
        persistentObject.modifiedDate = hashMap["modified_date"] as Long
        return persistentObject
    }

    companion object {
        const val DATABASE_TABLE_NAME = "MeasurementLogEntries"
    }
}