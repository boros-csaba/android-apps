package com.boroscsaba.dataaccess

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by Boros Csaba
 */

internal class DbHelper(context: Context, dbName: String, private val dbPatches: Array<IDbPatch>) : SQLiteOpenHelper(context, dbName, null, dbPatches.size) {

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        for (dbPatch in dbPatches) {
            dbPatch.applyPatch(sqLiteDatabase)
        }
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        for (i in oldVersion until dbPatches.size) {
            dbPatches[i].applyPatch(sqLiteDatabase)
        }
    }
}
