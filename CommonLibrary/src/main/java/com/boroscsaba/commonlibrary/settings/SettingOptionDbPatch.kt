package com.boroscsaba.commonlibrary.settings

import android.database.sqlite.SQLiteDatabase
import com.boroscsaba.dataaccess.IDbPatch

class SettingOptionDbPatch : IDbPatch {

    override fun applyPatch(db: SQLiteDatabase) {
        val query = "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "created_date DATE LONG, " +
                "modified_date DATE LONG, " +
                "setting_name char(250), " +
                "value DECIMAL(10,5) NOT NULL, " +
                "display_value char(250));"
        db.execSQL(query)
    }

    companion object {
        private const val DATABASE_TABLE_NAME = "Settings"
    }
}