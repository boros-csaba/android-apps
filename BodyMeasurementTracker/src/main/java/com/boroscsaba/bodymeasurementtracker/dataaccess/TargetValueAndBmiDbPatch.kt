package com.boroscsaba.bodymeasurementtracker.dataaccess


import android.database.sqlite.SQLiteDatabase
import com.boroscsaba.dataaccess.IDbPatch

class TargetValueAndBmiDbPatch: IDbPatch {
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
                "dashboard_stats_enabled INTEGER NOT NULL," +
                "target_value DOUBLE);"
        db.execSQL(parameterTableQuery)

        val insertLogEntries = "INSERT INTO " + ParameterMapper.DATABASE_TABLE_NAME +
                " SELECT id, name, unit, color, standard_icon, created_date, modified_date, parameter_order, preset_type, dashboard_dynamic, dashboard_dynamic_enabled, dashboard_currentValue, dashboard_currentValue_enabled, dashboard_stats, dashboard_stats_enabled, null " +
                "FROM tempParameters"
        db.execSQL(insertLogEntries)

        val insertBMI = "INSERT INTO " + ParameterMapper.DATABASE_TABLE_NAME +
                " VALUES(null, 'BMI', '', '#D32F2F', 0, " + System.currentTimeMillis() + ", " + System.currentTimeMillis() + ", 100, 101, 1, 0, 2, 0, 3, 0, null)"
        db.execSQL(insertBMI)

        db.execSQL(parameterTableQuery)
        db.execSQL("DROP TABLE IF EXISTS tempParameters")
    }
}