package com.boroscsaba.commonlibrary.tutorial

import android.database.sqlite.SQLiteDatabase
import com.boroscsaba.dataaccess.IDbPatch

class TutorialDbPatch : IDbPatch {

    override fun applyPatch(db: SQLiteDatabase) {
        val query = "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "created_date DATE LONG, " +
                "modified_date DATE LONG, " +
                "title char(250));"
        db.execSQL(query)
    }

    companion object {
        private const val DATABASE_TABLE_NAME = "TutorialLogItems"
    }
}