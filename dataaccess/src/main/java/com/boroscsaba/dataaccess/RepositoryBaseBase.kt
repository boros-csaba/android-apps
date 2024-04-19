package com.boroscsaba.dataaccess

import androidx.lifecycle.MutableLiveData
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.HashMap

/**
* Created by boros on 1/13/2018.
*/
abstract class RepositoryBaseBase<T : PersistentObject>(private val context: Context, private val dataAccess: DataAccess, private val classType: Class<*>, private val mapper: DataMapper<T>) {

    open fun upsert(persistentObject: T, alreadySynced: Boolean) : Int {
        persistentObject.modifiedDate = System.currentTimeMillis()
        var id = persistentObject.id
        if (persistentObject.guid == "") {
            persistentObject.guid = UUID.randomUUID().toString()
        }

        if (persistentObject.id == 0) {
            if (!alreadySynced) {
                persistentObject.createdDate = System.currentTimeMillis()
                persistentObject.modifiedDate = persistentObject.createdDate
            }
            val uri = context.contentResolver.insert(getUri(persistentObject.id), getContentValues(mapper.getProperties(persistentObject)))
            persistentObject.id = getId(uri)
            id = persistentObject.id
        }
        else {
            if (!alreadySynced){
                persistentObject.modifiedDate = System.currentTimeMillis()
            }
            context.contentResolver.update(getUri(persistentObject.id), getContentValues(mapper.getProperties(persistentObject)), "id = ?", arrayOf(persistentObject.id.toString()))
            persistentObject.id
        }
        return id
    }

    fun getObjects(where : String?, whereArgs: Array<String>?, sortOrder: String?, liveData: MutableLiveData<ArrayList<T>>) {
        DataObserverFactory<ArrayList<T>>(context).observe(mapper.getUri(), liveData) { getObjects(where, whereArgs, sortOrder) }
        AsyncTask().execute({
            val result = getObjects(where, whereArgs, sortOrder)
            Handler(Looper.getMainLooper()).post { liveData.value = result }
        })
    }

    fun getObjects(where : String?, whereArgs: Array<String>?, sortOrder: String?): ArrayList<T> {
        val result = ArrayList<HashMap<String, Any>>()
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(mapper.getUri(), getProjection(mapper), where, whereArgs, sortOrder)
            if (cursor != null) {
                cursor.moveToFirst()
                if (cursor.count > 0) {
                    do {
                        val row = HashMap<String, Any>()
                        for (property in mapper.getProperties()) {
                            val columnIndex = cursor.getColumnIndex(property.name)
                            when (property.type) {
                                PropertyType.Int -> row[property.name] = cursor.getInt(columnIndex)
                                PropertyType.String -> row[property.name] = cursor.getString(columnIndex)
                                PropertyType.Double -> row[property.name] = cursor.getDouble(columnIndex)
                                PropertyType.DateTime -> row[property.name] = cursor.getLong(columnIndex)
                                PropertyType.Boolean -> row[property.name] = cursor.getInt(columnIndex) != 0
                                PropertyType.Bitmap -> {
                                    var byteArray: ByteArray? = null
                                    if (!cursor.isNull(columnIndex)) {
                                        byteArray = cursor.getBlob(columnIndex)
                                    }
                                    val bitmap = if (byteArray != null) BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size) else android.graphics.Bitmap.createBitmap(1, 1, android.graphics.Bitmap.Config.ARGB_4444)
                                    row[property.name] = bitmap
                                }
                                PropertyType.Long -> row[property.name] = cursor.getLong(columnIndex)
                            }
                        }
                        result.add(row)
                    } while (cursor.moveToNext())
                }
            }
            cursor?.close()
        }
        finally {
            if (cursor?.isClosed == false) cursor.close()
        }
        return result.mapTo(ArrayList()) { mapper.map(it) }
    }

    fun getObjectById(id: Int, liveData: MutableLiveData<T?>) {
        DataObserverFactory<T?>(context).observe(getUri(id), liveData) { getObjectById(id) }
        AsyncTask().execute({
            val result = getObjectById(id)
            Handler(Looper.getMainLooper()).post { liveData.value = result }
        })
    }

    fun getObjectByIdOrDefault(id: Int): T {
        return getObjectById(id) ?: mapper.map(HashMap())
    }

    fun getObjectById(id: Int): T? {
        var result: HashMap<String, Any>? = null
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(getUri(id), getProjection(mapper), null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                if (cursor.count > 0) {
                    val row = HashMap<String, Any>()
                    for (property in mapper.getProperties()) {
                        val columnIndex = cursor.getColumnIndex(property.name)
                        when (property.type) {
                            PropertyType.Int -> row[property.name] = cursor.getInt(columnIndex)
                            PropertyType.String -> row[property.name] = cursor.getString(columnIndex)
                            PropertyType.Double -> row[property.name] = cursor.getDouble(columnIndex)
                            PropertyType.DateTime -> row[property.name] = cursor.getLong(columnIndex)
                            PropertyType.Boolean -> row[property.name] = cursor.getInt(columnIndex) != 0
                            PropertyType.Bitmap -> {
                                var byteArray: ByteArray? = null
                                if (!cursor.isNull(columnIndex)) {
                                    byteArray = cursor.getBlob(columnIndex)
                                }
                                val bitmap = if (byteArray != null) BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size) else android.graphics.Bitmap.createBitmap(1, 1, android.graphics.Bitmap.Config.ARGB_4444)
                                row[property.name] = bitmap
                            }
                            PropertyType.Long -> row[property.name] = cursor.getLong(columnIndex)
                        }
                    }
                    result = row
                }
            }
            cursor?.close()
        }
        finally {
            if (cursor?.isClosed == false) cursor.close()
        }
        return if (result != null) mapper.map(result) else null
    }

    fun mapChanges(item: T, changes: HashMap<String, Any>) {
        mapper.map(item, changes)
    }

    open fun delete(id: Int, alreadySynced: Boolean) {
        context.contentResolver.delete(getUri(id), "id = ?", arrayOf(id.toString()))
    }

    private fun getProjection(mapper: DataMapper<T>): Array<String> {
        return mapper.getProperties().map { p -> p.name }.toTypedArray()
    }

    private fun getUri(id: Int): Uri {
        return ContentUris.appendId(mapper.getUri().buildUpon(), id.toLong()).build()
    }

    private fun getContentValues(properties: List<Property<Any>>): ContentValues {
        val values = ContentValues()
        properties
                .filter { it.name != "id" }
                .forEach {
                    when (it.type) {
                        PropertyType.Int -> values.put(it.name, it.value as Int)
                        PropertyType.String -> values.put(it.name, it.value as String)
                        PropertyType.Double -> values.put(it.name, it.value as Double)
                        PropertyType.DateTime -> values.put(it.name, it.value as Long)
                        PropertyType.Boolean -> values.put(it.name, it.value as Boolean)
                        PropertyType.Bitmap -> {
                            val stream = ByteArrayOutputStream()
                            (it.value as android.graphics.Bitmap).compress(Bitmap.CompressFormat.PNG, 0, stream)
                            values.put(it.name, stream.toByteArray())
                        }
                        PropertyType.Long -> values.put(it.name, it.value as Long)
                    }
                }
        return values
    }

    private fun getId(uri: Uri?): Int {
        return uri?.lastPathSegment?.toIntOrNull() ?: 0
    }
}