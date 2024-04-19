package com.boroscsaba.myapplication.dataAccess

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.boroscsaba.commonlibrary.CircleBitmapTransformation
import com.boroscsaba.dataaccess.DataMapper
import com.boroscsaba.dataaccess.Property
import com.boroscsaba.dataaccess.PropertyType
import java.io.ByteArrayOutputStream

import java.util.ArrayList
import java.util.HashMap

/**
 * Created by Boros Csaba
 */

class GoalMapper(context: Context) : DataMapper<com.boroscsaba.myapplication.model.Goal>(context) {

    override val tableName = DATABASE_TABLE_NAME

    override fun getProperties(persistentObject: com.boroscsaba.myapplication.model.Goal?): List<Property<Any>> {
        val properties = ArrayList<Property<Any>>()

        var id = 0
        var createdDate: Long = 0
        var modifiedDate: Long = 0
        var guid = ""
        var title = ""
        var targetAmount = 0.0
        var initialAmount = 0.0
        var currency = "USD"
        var goalOrder = 0
        var icon = 0
        var dueDate: Long = 0
        var notificationEnabled = true
        if (persistentObject != null) {
            id = persistentObject.id
            createdDate = persistentObject.createdDate
            modifiedDate = persistentObject.modifiedDate
            guid = persistentObject.guid
            title = persistentObject.title
            targetAmount = persistentObject.targetAmount
            initialAmount = persistentObject.initialAmount
            currency = persistentObject.currencyCode
            goalOrder = persistentObject.goalOrder
            icon = persistentObject.icon
            dueDate = persistentObject.dueDate
            notificationEnabled = persistentObject.notificationEnabled
        }

        properties.add(Property("id", id, PropertyType.Int))
        properties.add(Property("created_date", createdDate, PropertyType.DateTime))
        properties.add(Property("modified_date", modifiedDate, PropertyType.DateTime))
        properties.add(Property("guid", guid, PropertyType.String))
        properties.add(Property("title", title, PropertyType.String))
        properties.add(Property("target_amount", targetAmount, PropertyType.Double))
        properties.add(Property("initial_amount", initialAmount, PropertyType.Double))
        properties.add(Property("currency", currency, PropertyType.String))
        properties.add(Property("goal_order", goalOrder, PropertyType.Int))
        properties.add(Property("icon", icon, PropertyType.Int))
        properties.add(Property("due_date", dueDate, PropertyType.DateTime))
        properties.add(Property("notification_enabled", notificationEnabled, PropertyType.Boolean))

        return properties
    }

    override fun map(hashMap: HashMap<String, Any>): com.boroscsaba.myapplication.model.Goal {
        return map(com.boroscsaba.myapplication.model.Goal(context), hashMap)
    }

    override fun map(persistentObject: com.boroscsaba.myapplication.model.Goal, hashMap: HashMap<String, Any>): com.boroscsaba.myapplication.model.Goal {
        if (hashMap.containsKey("id")) persistentObject.id = hashMap["id"] as Int
        if (hashMap.containsKey("created_date")) persistentObject.createdDate = getLong("created_date", hashMap)
        if (hashMap.containsKey("modified_date")) persistentObject.modifiedDate = getLong("modified_date", hashMap)
        if (hashMap.containsKey("guid")) persistentObject.guid = hashMap["guid"] as String
        if (hashMap.containsKey("title")) persistentObject.title = hashMap["title"] as String
        if (hashMap.containsKey("target_amount")) persistentObject.targetAmount = getDouble("target_amount", hashMap)
        if (hashMap.containsKey("initial_amount")) persistentObject.initialAmount = getDouble("initial_amount", hashMap)
        if (hashMap.containsKey("currency")) persistentObject.currencyCode = hashMap["currency"] as String
        if (hashMap.containsKey("goal_order")) persistentObject.goalOrder = hashMap["goal_order"] as Int
        if (hashMap.containsKey("icon")) persistentObject.icon = hashMap["icon"] as Int
        if (hashMap.containsKey("due_date")) persistentObject.dueDate = getLong("due_date", hashMap)
        if (hashMap.containsKey("notification_enabled")) persistentObject.notificationEnabled = hashMap["notification_enabled"] as Boolean
        return persistentObject
    }

    fun getImageUri(id: Int, modifiedDate: Long): Uri {
        val uri = Uri.parse("content://${context.packageName}.provider/$DATABASE_TABLE_NAME/Images/$modifiedDate")
        return ContentUris.appendId(uri.buildUpon(), id.toLong()).build()
    }

    fun getImageBase64String(id: Int, modifiedDate: Long): String {
        val inputStream = context.contentResolver.openInputStream(GoalMapper(context).getImageUri(id, modifiedDate))
        var bitmap = BitmapFactory.decodeStream(inputStream)
        if (bitmap.width > com.boroscsaba.myapplication.model.Goal.MAX_IMAGE_SIZE || bitmap.height > com.boroscsaba.myapplication.model.Goal.MAX_IMAGE_SIZE) {
            bitmap = CircleBitmapTransformation().transform(bitmap, com.boroscsaba.myapplication.model.Goal.MAX_IMAGE_SIZE)
        }
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.WEBP, 50, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.URL_SAFE).replace("\n","").trim()
    }

    companion object {
        const val DATABASE_TABLE_NAME = "Goals"
    }
}
