package com.boroscsaba.myapplication.dataAccess

import android.content.Context
import com.boroscsaba.dataaccess.DataMapper
import com.boroscsaba.dataaccess.Property
import com.boroscsaba.dataaccess.PropertyType

import java.util.ArrayList
import java.util.HashMap

/**
 * Created by Boros Csaba
 */

class TransactionMapper(context: Context) : DataMapper<com.boroscsaba.myapplication.model.Transaction>(context) {

    override val tableName = DATABASE_TABLE_NAME

    override fun getProperties(persistentObject: com.boroscsaba.myapplication.model.Transaction?): List<Property<Any>> {
        val properties = ArrayList<Property<Any>>()

        var id = 0
        var createdDate: Long = 0
        var modifiedDate: Long = 0
        var guid = ""
        var title = ""
        var amount = 0.0
        var date: Long = 0
        var goalId = 0
        if (persistentObject != null) {
            id = persistentObject.id
            createdDate = persistentObject.createdDate
            modifiedDate = persistentObject.modifiedDate
            guid = persistentObject.guid
            title = persistentObject.title
            amount = persistentObject.amount
            date = persistentObject.transactionDate
            goalId = persistentObject.goalId
        }

        properties.add(Property("id", id, PropertyType.Int))
        properties.add(Property("created_date", createdDate, PropertyType.DateTime))
        properties.add(Property("modified_date", modifiedDate, PropertyType.DateTime))
        properties.add(Property("guid", guid, PropertyType.String))
        properties.add(Property("title", title, PropertyType.String))
        properties.add(Property("amount", amount, PropertyType.Double))
        properties.add(Property("transaction_date", date, PropertyType.DateTime))
        properties.add(Property("goal_id", goalId, PropertyType.Int))

        return properties
    }

    override fun map(persistentObject: com.boroscsaba.myapplication.model.Transaction, hashMap: HashMap<String, Any>): com.boroscsaba.myapplication.model.Transaction {
        if (hashMap.containsKey("id")) persistentObject.id = hashMap["id"] as Int
        if (hashMap.containsKey("created_date")) persistentObject.createdDate = getLong("created_date", hashMap)
        if (hashMap.containsKey("modified_date")) persistentObject.modifiedDate = getLong("modified_date", hashMap)
        if (hashMap.containsKey("guid")) persistentObject.guid = hashMap["guid"] as String
        if (hashMap.containsKey("title")) persistentObject.title = hashMap["title"] as String
        if (hashMap.containsKey("amount")) persistentObject.amount = getDouble("amount", hashMap)
        if (hashMap.containsKey("transaction_date")) persistentObject.transactionDate = getLong("transaction_date", hashMap)
        if (hashMap.containsKey("goal_id")) persistentObject.goalId = hashMap["goal_id"] as Int
        return persistentObject
    }

    override fun map(hashMap: HashMap<String, Any>): com.boroscsaba.myapplication.model.Transaction {
        return map(com.boroscsaba.myapplication.model.Transaction(context), hashMap)
    }

    override fun getForeignKeyGuidIds(persistentObject: com.boroscsaba.myapplication.model.Transaction): HashMap<String, String> {
        val result = HashMap<String, String>()
        if (persistentObject.goalId > 0) {
            val goal = GoalRepository(context.applicationContext).getObjectById(persistentObject.goalId)
            result["goal_guid"] = goal!!.guid
        }
        return result
    }

    companion object {
        const val DATABASE_TABLE_NAME = "Transactions"
    }
}
