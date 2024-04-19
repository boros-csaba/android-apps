package com.boroscsaba.myweightlosstracker.dataAccess

import android.content.Context
import com.boroscsaba.dataaccess.DataMapper
import com.boroscsaba.dataaccess.Property
import com.boroscsaba.dataaccess.PropertyType
import com.boroscsaba.myweightlosstracker.model.Parameter

import java.util.ArrayList
import java.util.HashMap

/**
 * Created by Boros Csaba
 */

class ParameterMapper(context: Context) : DataMapper<Parameter>(context) {

    override val tableName = DATABASE_TABLE_NAME

    override fun getProperties(persistentObject: Parameter?): List<Property<Any>> {
        val properties = ArrayList<Property<Any>>()

		var id = 0
		var createdDate: Long = 0
		var modifiedDate: Long = 0
		var guid = ""
		var name = ""
		var goalValue = 0.0
		var unit = ""
		if (persistentObject != null) {
		id = persistentObject.id
		createdDate = persistentObject.createdDate
		modifiedDate = persistentObject.modifiedDate
		guid = persistentObject.guid
		name = persistentObject.name
		goalValue = persistentObject.goalValue
		unit = persistentObject.unit
	}

	properties.add(Property("id", id, PropertyType.Int))
	properties.add(Property("created_date", createdDate, PropertyType.DateTime))
	properties.add(Property("modified_date", modifiedDate, PropertyType.DateTime))
	properties.add(Property("guid", guid, PropertyType.String))
	properties.add(Property("name", name, PropertyType.String))
	properties.add(Property("goal_value", goalValue, PropertyType.Double))
	properties.add(Property("unit", unit, PropertyType.String))

	return properties
	}

	override fun map(persistentObject: Parameter, hashMap: HashMap<String, Any>): Parameter {
	 if (hashMap.containsKey("id")) persistentObject.id = hashMap["id"] as Int
	 if (hashMap.containsKey("created_date")) persistentObject.createdDate = getLong("created_date", hashMap)
	 if (hashMap.containsKey("modified_date")) persistentObject.modifiedDate = getLong("modified_date", hashMap)
	 if (hashMap.containsKey("guid")) persistentObject.guid = hashMap["guid"] as String
	 if (hashMap.containsKey("name")) persistentObject.name = hashMap["name"] as String
	 if (hashMap.containsKey("goal_value")) persistentObject.goalValue = getDouble("goal_value", hashMap)
	 if (hashMap.containsKey("unit")) persistentObject.unit = hashMap["unit"] as String
	 return persistentObject
	}

    override fun map(hashMap: HashMap<String, Any>): Parameter {
        return map(Parameter(context), hashMap)
    }

    companion object {
        const val DATABASE_TABLE_NAME = "Parameter"
    }
}