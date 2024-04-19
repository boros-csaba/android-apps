package com.boroscsaba.commonlibrary.tutorial

import android.content.Context
import com.boroscsaba.dataaccess.DataMapper
import com.boroscsaba.dataaccess.Property
import com.boroscsaba.dataaccess.PropertyType
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by boros on 1/13/2018.
 */
class TutorialLogItemMapper(context: Context) : DataMapper<TutorialLogItem>(context) {

    override val tableName = DATABASE_TABLE_NAME

    override fun getProperties(persistentObject: TutorialLogItem?): List<Property<Any>> {
        val properties = ArrayList<Property<Any>>()

        var id = 0
        var createdDate: Long = 0
        var modifiedDate: Long = 0
        var title = ""

        if (persistentObject != null) {
            id = persistentObject.id
            createdDate = persistentObject.createdDate
            modifiedDate = persistentObject.modifiedDate
            title = persistentObject.title
        }

        properties.add(Property("id", id, PropertyType.Int))
        properties.add(Property("created_date", createdDate, PropertyType.DateTime))
        properties.add(Property("modified_date", modifiedDate, PropertyType.DateTime))
        properties.add(Property("title", title, PropertyType.String))

        return properties
    }

    override fun map(persistentObject: TutorialLogItem, hashMap: HashMap<String, Any>): TutorialLogItem {
        if (hashMap.containsKey("id")) persistentObject.id = hashMap["id"] as Int
        if (hashMap.containsKey("created_date")) persistentObject.createdDate = getLong("created_date", hashMap)
        if (hashMap.containsKey("modified_date")) persistentObject.modifiedDate = getLong("modified_date", hashMap)
        if (hashMap.containsKey("title")) persistentObject.title = hashMap["title"] as String

        return persistentObject
    }

    override fun map(hashMap: HashMap<String, Any>): TutorialLogItem {
        return map(TutorialLogItem(context), hashMap)
    }

    companion object {
        const val DATABASE_TABLE_NAME = "TutorialLogItems"
    }
}