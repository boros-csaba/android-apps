package com.boroscsaba.englishirregularverbsmemorizer.dataAccess

import android.content.Context
import com.boroscsaba.dataaccess.DataMapper
import com.boroscsaba.dataaccess.Property
import com.boroscsaba.dataaccess.PropertyType
import com.boroscsaba.englishirregularverbsmemorizer.model.DailyResult

import java.util.ArrayList
import java.util.HashMap

/**
 * Created by Boros Csaba
 */

class DailyResultMapper(context: Context) : DataMapper<DailyResult>(context) {

    override val tableName = DATABASE_TABLE_NAME

    override fun getProperties(persistentObject: DailyResult?): List<Property<Any>> {
        val properties = ArrayList<Property<Any>>()

		var id = 0
		var createdDate: Long = 0
		var modifiedDate: Long = 0
		var guid = ""
		var day: Long = 0
		var correctGuesses = 0
		var missedGuesses = 0
		var goalMet = false
		if (persistentObject != null) {
			id = persistentObject.id
			createdDate = persistentObject.createdDate
			modifiedDate = persistentObject.modifiedDate
			guid = persistentObject.guid
			day = persistentObject.day
			correctGuesses = persistentObject.correctGuesses
			missedGuesses = persistentObject.missedGuesses
            goalMet = persistentObject.goalMet
		}

		properties.add(Property("id", id, PropertyType.Int))
		properties.add(Property("created_date", createdDate, PropertyType.DateTime))
		properties.add(Property("modified_date", modifiedDate, PropertyType.DateTime))
		properties.add(Property("guid", guid, PropertyType.String))
		properties.add(Property("day", day, PropertyType.DateTime))
		properties.add(Property("correct_guesses", correctGuesses, PropertyType.Int))
		properties.add(Property("missed_guesses", missedGuesses, PropertyType.Int))
        properties.add(Property("goal_met", goalMet, PropertyType.Boolean))

		return properties
	}

	override fun map(persistentObject: DailyResult, hashMap: HashMap<String, Any>): DailyResult {
	    if (hashMap.containsKey("id")) persistentObject.id = hashMap["id"] as Int
	    if (hashMap.containsKey("created_date")) persistentObject.createdDate = getLong("created_date", hashMap)
	    if (hashMap.containsKey("modified_date")) persistentObject.modifiedDate = getLong("modified_date", hashMap)
	    if (hashMap.containsKey("guid")) persistentObject.guid = hashMap["guid"] as String
	    if (hashMap.containsKey("day")) persistentObject.day = getLong("day", hashMap)
	    if (hashMap.containsKey("correct_guesses")) persistentObject.correctGuesses = hashMap["correct_guesses"] as Int
	    if (hashMap.containsKey("missed_guesses")) persistentObject.missedGuesses = hashMap["missed_guesses"] as Int
        if (hashMap.containsKey("goal_met")) persistentObject.goalMet = hashMap["goal_met"] as Boolean
	    return persistentObject
	}

    override fun map(hashMap: HashMap<String, Any>): DailyResult {
        return map(DailyResult(context), hashMap)
    }

    companion object {
        const val DATABASE_TABLE_NAME = "DailyResult"
    }
}