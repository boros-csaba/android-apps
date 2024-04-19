package com.boroscsaba.bodymeasurementtracker.dataaccess

import android.database.sqlite.SQLiteDatabase
import com.boroscsaba.dataaccess.IDbPatch

class RemoveMeasurementLogDbPatch: IDbPatch {
    override fun applyPatch(db: SQLiteDatabase) {

        db.execSQL("DROP TABLE IF EXISTS tempMeasurements")
        db.execSQL("ALTER TABLE ${MeasurementLogEntryMapper.DATABASE_TABLE_NAME} RENAME TO tempMeasurements")
        val measurementLogEntryQuery = "CREATE TABLE IF NOT EXISTS " + MeasurementLogEntryMapper.DATABASE_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "parameterId INTEGER NOT NULL," +
                "entry_value DOUBLE NOT NULL," +
                "log_date DATE LONG," +
                "created_date DATE LONG," +
                "modified_date DATE LONG);"
        db.execSQL(measurementLogEntryQuery)
        val insertNewMeasurements = "INSERT INTO " + MeasurementLogEntryMapper.DATABASE_TABLE_NAME +
                " SELECT tempMeasurements.id, parameterId, entry_value, log_date, created_date, modified_date " +
                "FROM tempMeasurements inner join MeasurementLogs on MeasurementLogs.id = tempMeasurements.measurementLogId"
        db.execSQL(insertNewMeasurements)
        db.execSQL("DROP TABLE IF EXISTS tempMeasurements")
        db.execSQL("DROP TABLE IF EXISTS MeasurementLogs")
    }
}