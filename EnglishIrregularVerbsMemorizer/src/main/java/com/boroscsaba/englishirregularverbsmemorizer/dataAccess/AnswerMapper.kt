package com.boroscsaba.englishirregularverbsmemorizer.dataAccess

import android.content.Context
import com.boroscsaba.dataaccess.DataMapper
import com.boroscsaba.dataaccess.Property
import com.boroscsaba.dataaccess.PropertyType
import com.boroscsaba.englishirregularverbsmemorizer.model.Answer

import java.util.ArrayList
import java.util.HashMap

/**
 * Created by Boros Csaba
 */

class AnswerMapper(context: Context) : DataMapper<Answer>(context) {

    override val tableName = DATABASE_TABLE_NAME

    override fun getProperties(persistentObject: Answer?): List<Property<Any>> {
        val properties = ArrayList<Property<Any>>()

		var id = 0
		var createdDate: Long = 0
		var modifiedDate: Long = 0
		var guid = ""
        var verbId = 0
		var correctAnswers = 0
		var wrongAnswers = 0
		if (persistentObject != null) {
            id = persistentObject.id
            createdDate = persistentObject.createdDate
            modifiedDate = persistentObject.modifiedDate
            guid = persistentObject.guid
            verbId = persistentObject.verbId
            correctAnswers = persistentObject.correctAnswers
            wrongAnswers = persistentObject.wrongAnswers
        }

        properties.add(Property("id", id, PropertyType.Int))
        properties.add(Property("created_date", createdDate, PropertyType.DateTime))
        properties.add(Property("modified_date", modifiedDate, PropertyType.DateTime))
        properties.add(Property("guid", guid, PropertyType.String))
        properties.add(Property("verb_id", verbId, PropertyType.Int))
        properties.add(Property("correct_answers", correctAnswers, PropertyType.Int))
        properties.add(Property("wrong_answers", wrongAnswers, PropertyType.Int))

        return properties
	}

	override fun map(persistentObject: Answer, hashMap: HashMap<String, Any>): Answer {
        if (hashMap.containsKey("id")) persistentObject.id = hashMap["id"] as Int
        if (hashMap.containsKey("created_date")) persistentObject.createdDate = getLong("created_date", hashMap)
        if (hashMap.containsKey("modified_date")) persistentObject.modifiedDate = getLong("modified_date", hashMap)
        if (hashMap.containsKey("guid")) persistentObject.guid = hashMap["guid"] as String
        if (hashMap.containsKey("verb_id")) persistentObject.verbId = hashMap["verb_id"] as Int
        if (hashMap.containsKey("correct_answers")) persistentObject.correctAnswers = hashMap["correct_answers"] as Int
        if (hashMap.containsKey("wrong_answers")) persistentObject.wrongAnswers = hashMap["wrong_answers"] as Int
        return persistentObject
	}

    override fun map(hashMap: HashMap<String, Any>): Answer {
        return map(Answer(context), hashMap)
    }

    companion object {
        const val DATABASE_TABLE_NAME = "Answer"
    }
}