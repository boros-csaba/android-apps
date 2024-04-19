package com.boroscsaba.myweightlosstracker.dataAccess

import android.database.sqlite.SQLiteDatabase

import com.boroscsaba.dataaccess.IDbPatch

/**
 * Created by Boros Csaba
 */

internal class InitialDbPatch : IDbPatch {

    override fun applyPatch(db: SQLiteDatabase) {

        val parameterTableQuery = "CREATE TABLE IF NOT EXISTS " + ParameterMapper.DATABASE_TABLE_NAME + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "created_date DATE LONG, " +
                "modified_date DATE LONG, " +
                "guid VARCHAR(255), " +
                "name VARCHAR(255), " +
                "goal_value DOUBLE NOT NULL, " +
                "unit VARCHAR(255));"
        db.execSQL(parameterTableQuery)

        val measurementTableQuery = "CREATE TABLE IF NOT EXISTS " + MeasurementMapper.DATABASE_TABLE_NAME + "(" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"created_date DATE LONG, " +
				"modified_date DATE LONG, " +
				"guid VARCHAR(255), " +
				"value DOUBLE NOT NULL, " +
				"parameter_id INTEGER NOT NULL, " +
				"log_date DATE LONG);"
		db.execSQL(measurementTableQuery)
	}
}