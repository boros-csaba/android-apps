package com.boroscsaba.myapplication.dataAccess

import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.commonlibrary.cloudsync.CloudSyncServiceBase
import org.json.JSONStringer
import java.util.*
import kotlin.collections.HashMap


class CloudSyncService(private val application: ApplicationBase): CloudSyncServiceBase(application) {

    override fun getInitialChangeString(): String {
        val goalRepository = GoalRepository(application)
        val transactionRepository = TransactionRepository(application)

        val goals = goalRepository.getObjects(null, null, null)
        goals.filter { goal -> goal.guid == "" }.forEach { goal ->
            goal.guid = UUID.randomUUID().toString()
            goalRepository.upsert(goal, true)
        }
        val transactions = transactionRepository.getObjects(null, null, null)
        transactions.filter { transaction -> transaction.guid == "" }.forEach { transaction ->
            transaction.guid = UUID.randomUUID().toString()
            transactionRepository.upsert(transaction, true)
        }

        var jsonStringer = JSONStringer()
                .`object`()
                .key("items").array()

        goals.forEach { goal ->
            jsonStringer = jsonStringer.value(goalRepository.getNewObjectChangeString(goal))
        }
        transactions.forEach { transaction ->
            jsonStringer = jsonStringer.value(transactionRepository.getNewObjectChangeString(transaction))
        }

        jsonStringer = jsonStringer.endArray()
                .endObject()
        return jsonStringer.toString()
    }

    override fun getInitialImages(): ArrayList<Pair<String, String>> {
        val result = ArrayList<Pair<String, String>>()
        val goals = GoalRepository(application).getObjects(null, null, null)
        goals.filter { goal -> goal.icon == 1000 }
                .forEach { goal ->
                    val jsonStringer = JSONStringer().`object`()
                            .key("object_type").value(GoalMapper(application).tableName)
                            .key("guid").value(goal.guid)
                            .key("data").value(GoalMapper(application).getImageBase64String(goal.id, goal.modifiedDate))
                            .endObject()
                    result.add(Pair(goal.guid, jsonStringer.toString()))
                }
        return result
    }

    override fun processSyncedObject(type: String, operation: String, properties: HashMap<String, Any>) {
        val goalRepository = GoalRepository(application)
        val transactionRepository = TransactionRepository(application)
        if (type == GoalMapper.DATABASE_TABLE_NAME) {
            when (operation) {
                "add" -> {
                    val goal = GoalMapper(application).map(properties)
                    goal.id = 0
                    goalRepository.upsert(goal, true)
                    if (goal.icon == 1000) {
                        changeImage(goal)
                    }
                }
                "update" -> {
                    val original = goalRepository.getObjects("guid = ?", arrayOf(properties["guid"] as String), null).first()
                    val goal = GoalMapper(application).map(original, properties)
                    goalRepository.upsert(goal,true)
                    if (goal.icon == 1000) {
                        changeImage(goal)
                    }
                }
                "delete" -> {
                    val goal = goalRepository.getObjects("guid = ?", arrayOf(properties["guid"] as String), null).firstOrNull()
                    if (goal != null) {
                        goalRepository.delete(goal.id, true)
                    }
                }
            }
        }
        else if (type == TransactionMapper.DATABASE_TABLE_NAME) {
            when (operation) {
                "add" -> {
                    val goal = goalRepository.getObjects("guid = ?", arrayOf(properties["goal_guid"] as String), null).firstOrNull()
                    if (goal != null) {
                        val transaction = TransactionMapper(application).map(properties)
                        transaction.id = 0
                        transaction.goalId = goal.id
                        transactionRepository.upsert(transaction, true)
                    }
                    else {
                        LoggingHelper.logException(Exception("CloudSync goal not found!"), application)
                    }
                }
                "update" -> {
                    val original = transactionRepository.getObjects("guid = ?", arrayOf(properties["guid"] as String), null).first()
                    val transaction = TransactionMapper(application).map(original, properties)
                    transactionRepository.upsert(transaction,true)
                }
                "delete" -> {
                    val transaction = transactionRepository.getObjects("guid = ?", arrayOf(properties["guid"] as String), null).first()
                    transactionRepository.delete(transaction.id, true)
                }
            }
        }
    }

    private fun changeImage(goal: com.boroscsaba.myapplication.model.Goal) {
        setImage(goal.guid) { byteArray ->
            val stream = application.contentResolver.openOutputStream(GoalMapper(application).getImageUri(goal.id, goal.modifiedDate))
            if (stream != null) {
                stream.write(byteArray)
                stream.close()
                GoalRepository(application).upsert(goal, true)
            }
        }
    }
}