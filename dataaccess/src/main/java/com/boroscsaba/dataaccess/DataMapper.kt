package com.boroscsaba.dataaccess

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import java.util.HashMap

/**
 * Created by Boros Csaba
 */

abstract class DataMapper<T : PersistentObject>(protected val context: Context) {
    abstract val tableName: String
    abstract fun getProperties(persistentObject: T?): List<Property<Any>>
    abstract fun map(hashMap: HashMap<String, Any>): T
    abstract fun map(persistentObject: T, hashMap: HashMap<String, Any>): T

    fun getUri(id: Int? = null): Uri {
        val uri = Uri.parse("content://${context.packageName}.provider/$tableName")
        if (id != null) {
            ContentUris.appendId(uri.buildUpon(), id.toLong())
        }
        return uri
    }

    fun getProperties(): List<Property<Any>> {
        return getProperties(null)
    }

    open fun getForeignKeyGuidIds(persistentObject: T): HashMap<String, String> {
        return HashMap()
    }

    fun getDouble(name: String, hashMap: HashMap<String, Any>): Double {
        return when {
            hashMap[name] is Double -> return hashMap[name] as Double
            hashMap[name] is Int -> (hashMap[name] as Int).toDouble()
            else -> 0.0
        }
    }

    fun getLong(name: String, hashMap: HashMap<String, Any>): Long {
        return when {
            hashMap[name] is Long -> return hashMap[name] as Long
            hashMap[name] is Int -> (hashMap[name] as Int).toLong()
            else -> 0
        }
    }
}
