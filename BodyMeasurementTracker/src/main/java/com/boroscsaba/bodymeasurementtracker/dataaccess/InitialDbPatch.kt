package com.boroscsaba.bodymeasurementtracker.dataaccess

import android.database.sqlite.SQLiteDatabase

import com.boroscsaba.dataaccess.IDbPatch

/**
 * Created by Boros Csaba
 */

internal class InitialDbPatch : IDbPatch {

    override fun applyPatch(db: SQLiteDatabase) {

        val parameterTableQuery = "CREATE TABLE IF NOT EXISTS " + ParameterMapper.DATABASE_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name VARCHAR(255)," +
                "unit VARCHAR(20)," +
                "color VARCHAR(10)," +
                "standard_icon INTEGER NOT NULL," +
                "created_date DATE LONG," +
                "modified_date DATE LONG, " +
                "parameter_order INTEGER NOT NULL," +
                "preset_type INTEGER NOT NULL);"
        db.execSQL(parameterTableQuery)

        val measurementLogQuery = "CREATE TABLE IF NOT EXISTS MeasurementLogs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "photo BLOB," +
                "description VARCHAR(500)," +
                "created_date DATE LONG, " +
                "modified_date DATE LONG, " +
                "log_date DATE LONG);"
        db.execSQL(measurementLogQuery)

        val measurementLogEntryQuery = "CREATE TABLE IF NOT EXISTS " + MeasurementLogEntryMapper.DATABASE_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "parameterId INTEGER NOT NULL," +
                "measurementLogId INTEGER NOT NULL," +
                "entry_value DOUBLE NOT NULL);"
        db.execSQL(measurementLogEntryQuery)
    }
}
