package com.boroscsaba.commonlibrary.settings

import android.content.Context
import com.boroscsaba.dataaccess.DataMapper
import com.boroscsaba.dataaccess.Property
import com.boroscsaba.dataaccess.PropertyType

/**
 * Created by boros on 1/13/2018.
 */
class SettingOptionMapper(context: Context) : DataMapper<SettingOption>(context) {

    override val tableName = DATABASE_TABLE_NAME

    override fun getProperties(persistentObject: SettingOption?): List<Property<Any>> {
        val properties = ArrayList<Property<Any>>()

        var id = 0
        var createdDate: Long = 0
        var modifiedDate: Long = 0
        var settingName = ""
        var value = 0.0
        var displayValue = ""

        if (persistentObject != null) {
            id = persistentObject.id
            createdDate = persistentObject.createdDate
            modifiedDate = persistentObject.modifiedDate
            settingName = persistentObject.settingName
            value = persistentObject.value
            displayValue = persistentObject.displayValue
        }

        properties.add(Property("id", id, PropertyType.Int))
        properties.add(Property("created_date", createdDate, PropertyType.DateTime))
        properties.add(Property("modified_date", modifiedDate, PropertyType.DateTime))
        properties.add(Property("setting_name", settingName, PropertyType.String))
        properties.add(Property("value", value, PropertyType.Double))
        properties.add(Property("display_value", displayValue, PropertyType.String))

        return properties
    }

    override fun map(persistentObject: SettingOption, hashMap: java.util.HashMap<String, Any>): SettingOption {
        if (hashMap.containsKey("id")) persistentObject.id = hashMap["id"] as Int
        if (hashMap.containsKey("created_date")) persistentObject.createdDate = getLong("created_date", hashMap)
        if (hashMap.containsKey("modified_date")) persistentObject.modifiedDate = getLong("modified_date", hashMap)
        if (hashMap.containsKey("setting_name")) persistentObject.settingName = hashMap["setting_name"] as String
        if (hashMap.containsKey("value")) persistentObject.value = getDouble("value", hashMap)
        if (hashMap.containsKey("display_value")) persistentObject.displayValue = hashMap["display_value"] as String

        return persistentObject
    }

    override fun map(hashMap: HashMap<String, Any>): SettingOption {
        return map(SettingOption(context), hashMap)
    }

    companion object {
        const val DATABASE_TABLE_NAME = "Settings"
    }
}