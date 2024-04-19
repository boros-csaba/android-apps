package com.boroscsaba.myweightlosstracker.dataAccess

import android.content.Context
import com.boroscsaba.dataaccess.DataMapper
import com.boroscsaba.dataaccess.Property
import com.boroscsaba.dataaccess.PropertyType
import com.boroscsaba.myweightlosstracker.model.Measurement

import java.util.ArrayList
import java.util.HashMap

/**
 * Created by Boros Csaba
 */

class MeasurementMapper(context: Context) : DataMapper<Measurement>(context) {

    override val tableName = DATABASE_TABLE_NAME

    override fun getProperties(persistentObject: Measurement?): List<Property<Any>> {
        val properties = ArrayList<Property<Any>>()

		var id = 0
		var createdDate: Long = 0
		var modifiedDate: Long = 0
		var guid = ""
		var value = 0.0
		var parameterId = 0
		var logDate:Long = 0
		if (persistentObject != null) {
		id = persistentObject.id
		createdDate = persistentObject.createdDate
		modifiedDate = persistentObject.modifiedDate
		guid = persistentObject.guid
		//value = persistentObject.value
		parameterId = persistentObject.parameterId
		logDate = persistentObject.logDate
	}

	properties.add(Property("id", id, PropertyType.Int))
	properties.add(Property("created_date", createdDate, PropertyType.DateTime))
	properties.add(Property("modified_date", modifiedDate, PropertyType.DateTime))
	properties.add(Property("guid", guid, PropertyType.String))
	properties.add(Property("value", value, PropertyType.Double))
	properties.add(Property("parameter_id", parameterId, PropertyType.Int))
	properties.add(Property("log_date", logDate, PropertyType.DateTime))

	return properties
	}

	override fun map(persistentObject: Measurement, hashMap: HashMap<String, Any>): Measurement {
	 if (hashMap.containsKey("id")) persistentObject.id = hashMap["id"] as Int
	 if (hashMap.containsKey("created_date")) persistentObject.createdDate = getLong("created_date", hashMap)
	 if (hashMap.containsKey("modified_date")) persistentObject.modifiedDate = getLong("modified_date", hashMap)
	 if (hashMap.containsKey("guid")) persistentObject.guid = hashMap["guid"] as String
	 //if (hashMap.containsKey("value")) persistentObject.value = getDouble("value", hashMap)
	 if (hashMap.containsKey("parameter_id")) persistentObject.parameterId = hashMap["parameter_id"] as Int
	 if (hashMap.containsKey("log_date")) persistentObject.logDate = getLong("log_date", hashMap)
	 return persistentObject
	}

    override fun map(hashMap: HashMap<String, Any>): Measurement {
        return map(Measurement(context), hashMap)
    }

    companion object {
        const val DATABASE_TABLE_NAME = "Measurements"
    }
}