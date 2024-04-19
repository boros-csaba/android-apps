package com.boroscsaba.bodymeasurementtracker.dataaccess

import android.database.sqlite.SQLiteDatabase
import com.boroscsaba.dataaccess.IDbPatch

internal class EmptyLogItemDbPatch : IDbPatch {

    override fun applyPatch(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS tempMeasurementLogEntry")
        db.execSQL("ALTER TABLE ${MeasurementLogEntryMapper.DATABASE_TABLE_NAME} RENAME TO tempMeasurementLogEntry")

        val measurementLogEntryQuery = "CREATE TABLE IF NOT EXISTS " + MeasurementLogEntryMapper.DATABASE_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "parameterId INTEGER NOT NULL," +
                "measurementLogId INTEGER NOT NULL," +
                "entry_value DOUBLE NOT NULL," +
                "is_empty INTEGER NOT NULL);"
        db.execSQL(measurementLogEntryQuery)

        val insertLogEntries = "INSERT INTO " + MeasurementLogEntryMapper.DATABASE_TABLE_NAME +
                " SELECT id, parameterId, measurementLogId, entry_value, 0 " +
                "FROM tempMeasurementLogEntry"
        db.execSQL(insertLogEntries)
        db.execSQL("DROP TABLE IF EXISTS tempMeasurementLogEntry")
    }
}