package com.boroscsaba.bodymeasurementtracker.dataaccess

import android.database.sqlite.SQLiteDatabase
import com.boroscsaba.dataaccess.IDbPatch

internal  class ParameterDashboardSettingsDbPatch: IDbPatch {
    override fun applyPatch(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS tempParameters")
        db.execSQL("ALTER TABLE ${ParameterMapper.DATABASE_TABLE_NAME} RENAME TO tempParameters")

        val parameterTableQuery = "CREATE TABLE IF NOT EXISTS " + ParameterMapper.DATABASE_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name VARCHAR(255)," +
                "unit VARCHAR(20)," +
                "color VARCHAR(10)," +
                "standard_icon INTEGER NOT NULL," +
                "created_date DATE LONG," +
                "modified_date DATE LONG, " +
                "parameter_order INTEGER NOT NULL," +
                "preset_type INTEGER NOT NULL," +
                "dashboard_dynamic INTEGER NOT NULL," +
                "dashboard_dynamic_enabled INTEGER NOT NULL," +
                "dashboard_currentValue INTEGER NOT NULL," +
                "dashboard_currentValue_enabled INTEGER NOT NULL," +
                "dashboard_stats INTEGER NOT NULL," +
                "dashboard_stats_enabled INTEGER NOT NULL);"
        db.execSQL(parameterTableQuery)

        val insertLogEntries = "INSERT INTO " + ParameterMapper.DATABASE_TABLE_NAME +
                " SELECT id, name, unit, color, standard_icon, created_date, modified_date, parameter_order, preset_type, 1, 0, 2, 1, 3, 0 " +
                "FROM tempParameters"
        db.execSQL(insertLogEntries)

        db.execSQL(parameterTableQuery)
        db.execSQL("DROP TABLE IF EXISTS tempParameters")
    }
}