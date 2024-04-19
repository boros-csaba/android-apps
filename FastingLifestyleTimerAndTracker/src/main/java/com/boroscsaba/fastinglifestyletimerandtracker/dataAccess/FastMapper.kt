package com.boroscsaba.fastinglifestyletimerandtracker.dataAccess

import android.content.Context
import com.boroscsaba.dataaccess.DataMapper
import com.boroscsaba.dataaccess.Property
import com.boroscsaba.dataaccess.PropertyType
import com.boroscsaba.fastinglifestyletimerandtracker.model.Fast

import java.util.ArrayList
import java.util.HashMap

/**
 * Created by Boros Csaba
 */

internal class FastMapper(private val application: Context) : DataMapper<Fast>(application) {
    override val tableName = DATABASE_TABLE_NAME

    override fun getProperties(persistentObject: Fast?): List<Property<Any>> {
        val properties = ArrayList<Property<Any>>()

        var id = 0
        var createdDate: Long = 0
        var modifiedDate: Long = 0
        var startDate: Long = 0
        var endDate: Long = 0
        var targetHours = 0
        var targetMinutes = 0
        var note = ""

        if (persistentObject != null) {
            id = persistentObject.id
            createdDate = persistentObject.createdDate
            modifiedDate = persistentObject.modifiedDate
            startDate = persistentObject.startDate
            endDate = persistentObject.endDate
            targetHours = persistentObject.targetHours
            targetMinutes = persistentObject.targetMinutes
            note = persistentObject.note
        }

        properties.add(Property("id", id, PropertyType.Int))
        properties.add(Property("created_date", createdDate, PropertyType.DateTime))
        properties.add(Property("modified_date", modifiedDate, PropertyType.DateTime))
        properties.add(Property("start_date", startDate, PropertyType.DateTime))
        properties.add(Property("end_date", endDate, PropertyType.DateTime))
        properties.add(Property("target_hours", targetHours, PropertyType.Int))
        properties.add(Property("target_minutes", targetMinutes, PropertyType.Int))
        properties.add(Property("note", note, PropertyType.String))

        return properties
    }

    override fun map(hashMap: HashMap<String, Any>): Fast {
        val fast = Fast(application)
        return map(fast, hashMap)
    }

    override fun map(persistentObject: Fast, hashMap: HashMap<String, Any>): Fast {
        persistentObject.id = hashMap["id"] as Int
        persistentObject.createdDate = hashMap["created_date"] as Long
        persistentObject.modifiedDate = hashMap["modified_date"] as Long
        persistentObject.startDate = hashMap["start_date"] as Long
        persistentObject.endDate = hashMap["end_date"] as Long
        persistentObject.targetHours = hashMap["target_hours"] as Int
        persistentObject.targetMinutes = hashMap["target_minutes"] as Int
        persistentObject.note = hashMap["note"] as String
        return persistentObject
    }

    companion object {
        const val DATABASE_TABLE_NAME = "Fasts"
    }
}
