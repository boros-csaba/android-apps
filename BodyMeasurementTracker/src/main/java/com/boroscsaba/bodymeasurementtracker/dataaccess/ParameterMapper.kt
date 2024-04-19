package com.boroscsaba.bodymeasurementtracker.dataaccess

import android.content.Context
import com.boroscsaba.bodymeasurementtracker.MeasurementPresetTypeEnum
import com.boroscsaba.bodymeasurementtracker.model.Parameter
import com.boroscsaba.dataaccess.DataMapper
import com.boroscsaba.dataaccess.Property
import com.boroscsaba.dataaccess.PropertyType

import java.util.ArrayList
import java.util.HashMap

/**
 * Created by Boros Csaba
 */

internal class ParameterMapper(private val application: Context) : DataMapper<Parameter>(application) {
    override val tableName = DATABASE_TABLE_NAME

    override fun getProperties(persistentObject: Parameter?): List<Property<Any>> {
        val properties = ArrayList<Property<Any>>()

        var id = 0
        var name = ""
        var unit = ""
        var color = ""
        var standardIcon = 0
        var createdDate: Long = 0
        var modifiedDate: Long = 0
        var presetType = 0
        var dashboardOrder = 0
        var dashboardEnabled = true
        var targetValue: Double = -1.0
        var targetDueDate: Long = 0

        if (persistentObject != null) {
            id = persistentObject.id
            name = persistentObject.name
            unit = persistentObject.unit
            color = persistentObject.color
            standardIcon = persistentObject.standardIcon
            createdDate = persistentObject.createdDate
            modifiedDate = persistentObject.modifiedDate
            presetType = persistentObject.presetType.value
            dashboardOrder = persistentObject.dashboardOrder
            dashboardEnabled = persistentObject.dashboardEnabled
            targetValue = persistentObject.targetValue
            targetDueDate = persistentObject.targetDueDate
        }

        properties.add(Property("id", id, PropertyType.Int))
        properties.add(Property("name", name, PropertyType.String))
        properties.add(Property("unit", unit, PropertyType.String))
        properties.add(Property("color", color, PropertyType.String))
        properties.add(Property("standard_icon", standardIcon, PropertyType.Int))
        properties.add(Property("created_date", createdDate, PropertyType.DateTime))
        properties.add(Property("modified_date", modifiedDate, PropertyType.DateTime))
        properties.add(Property("preset_type", presetType, PropertyType.Int))
        properties.add(Property("dashboard_order", dashboardOrder, PropertyType.Int))
        properties.add(Property("dashboard_enabled", dashboardEnabled, PropertyType.Boolean))
        properties.add(Property("target_value", targetValue, PropertyType.Double))
        properties.add(Property("target_due_date", targetDueDate, PropertyType.DateTime))

        return properties
    }

    override fun map(hashMap: HashMap<String, Any>): Parameter {
        return map(Parameter(application), hashMap)
    }

    override fun map(persistentObject: Parameter, hashMap: HashMap<String, Any>): Parameter {
        persistentObject.id = hashMap["id"] as Int
        persistentObject.name = hashMap["name"] as String
        persistentObject.unit = hashMap["unit"] as String
        persistentObject.color = hashMap["color"] as String
        persistentObject.standardIcon = hashMap["standard_icon"] as Int
        persistentObject.createdDate = hashMap["created_date"] as Long
        persistentObject.modifiedDate = hashMap["modified_date"] as Long
        persistentObject.presetType = MeasurementPresetTypeEnum.fromInt(hashMap["preset_type"] as Int)
        persistentObject.dashboardOrder = hashMap["dashboard_order"] as Int
        persistentObject.dashboardEnabled = hashMap["dashboard_enabled"] as Boolean
        persistentObject.targetValue = getDouble("target_value", hashMap)
        persistentObject.targetDueDate = hashMap["target_due_date"] as Long
        return persistentObject
    }

    companion object {
        const val DATABASE_TABLE_NAME = "Parameters"
    }
}
