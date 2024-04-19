package com.boroscsaba.dataaccess

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

import java.util.HashMap


class DataAccess(context: Context, dbName: String, dbPatches: Array<IDbPatch>) {

    private val dataMappers: HashMap<String, DataMapper<PersistentObject>> = HashMap()
    private val dbHelper : DbHelper = DbHelper(context, dbName, dbPatches)

    fun registerDataMapper(mappedClass: Class<*>, dataMapper: DataMapper<PersistentObject>) {
        dataMappers[mappedClass.name] = dataMapper
    }

    fun insert(tableName: String, values: ContentValues?) : Int {
        val db = dbHelper.writableDatabase
        val id = db.insert(tableName, null, values)
        return id.toInt()
    }

    fun update(tableName: String, values: ContentValues?, where: String?, whereArgs: Array<out String>?) : Int {
        val db = getWritableDatabase()
        return db.update(tableName, values, where, whereArgs)
    }

    fun getObjects(tableName: String, projection: Array<out String>?, where : String?, whereArgs: Array<out String>?, sortOrder: String?): Cursor {
        val db = getReadableDatabase()
        return db.query(tableName, projection, where, whereArgs, null, null, sortOrder)
    }

    fun getObjectById(tableName: String, id: String, projection: Array<out String>?): Cursor {
        val db = getReadableDatabase()
        return db.query(tableName, projection, "id = ?", arrayOf(id), null, null, null)
    }

    fun deleteObject(tableName: String, where: String?, whereArgs: Array<out String>?): Int {
        val db = getWritableDatabase()
        return db.delete(tableName, where, whereArgs)
    }

    private var readableDatabase: SQLiteDatabase? = null
    private fun getReadableDatabase(): SQLiteDatabase {
        val database = readableDatabase
        return if (database == null) {
            val newDatabase = dbHelper.readableDatabase
            readableDatabase = newDatabase
            newDatabase
        }
        else {
            if (database.isOpen) {
                database
            } else {
                val newDatabase = dbHelper.readableDatabase
                readableDatabase = newDatabase
                newDatabase
            }
        }
    }

    private var writableDatabase: SQLiteDatabase? = null
    private fun getWritableDatabase(): SQLiteDatabase {
        val database = writableDatabase
        return if (database == null) {
            val newDatabase = dbHelper.writableDatabase
            writableDatabase = newDatabase
            newDatabase
        }
        else {
            if (database.isOpen) {
                database
            } else {
                val newDatabase = dbHelper.writableDatabase
                writableDatabase = newDatabase
                newDatabase
            }
        }
    }
}
