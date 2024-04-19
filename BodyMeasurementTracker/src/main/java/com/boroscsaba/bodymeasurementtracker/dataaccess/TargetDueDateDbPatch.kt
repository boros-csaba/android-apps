package com.boroscsaba.bodymeasurementtracker.dataaccess

import android.database.sqlite.SQLiteDatabase
import com.boroscsaba.dataaccess.IDbPatch

class TargetDueDateDbPatch: IDbPatch {
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
                "preset_type INTEGER NOT NULL," +
                "dashboard_order INTEGER NOT NULL," +
                "dashboard_enabled INTEGER NOT NULL," +
                "target_value DOUBLE NOT NULL," +
                "target_due_date DATE LONG);"
        db.execSQL(parameterTableQuery)

        val insertParameters = "INSERT INTO " + ParameterMapper.DATABASE_TABLE_NAME +
                " SELECT id, name, unit, color, standard_icon, created_date, modified_date, preset_type, dashboard_currentValue, dashboard_currentValue_enabled, -1, 0 " +
                "FROM tempParameters"
        db.execSQL(insertParameters)


        val cursor = db.query("Settings", arrayOf("value, display_value"), "setting_name = ?", arrayOf("HEIGHT_SETTINGS"), null, null, null)
        if (cursor.moveToFirst()) {
            val height = cursor.getDouble(cursor.getColumnIndex("value"))
            val unit = cursor.getString(cursor.getColumnIndex("display_value"))?.split(' ')?.lastOrNull()
            val innerCursor = db.query(ParameterMapper.DATABASE_TABLE_NAME, arrayOf("id"), "preset_type = ?", arrayOf("1"), null, null, null)
            if (!innerCursor.moveToFirst()) {
                val insertHeight = "INSERT INTO " + ParameterMapper.DATABASE_TABLE_NAME +
                        " VALUES(null, 'Height', '$unit', '#FF9800', 0, " + System.currentTimeMillis() + ", " + System.currentTimeMillis() + ", 1, 10, 0, -1, 0)"
                db.execSQL(insertHeight)
                val innerCursor2 = db.query(ParameterMapper.DATABASE_TABLE_NAME, arrayOf("id"), "preset_type = ?", arrayOf("1"), null, null, null)
                if (innerCursor2.moveToFirst()) {
                    val id = innerCursor2.getInt(innerCursor2.getColumnIndex("id"))
                    val insertLogEntry = "INSERT INTO " + MeasurementLogEntryMapper.DATABASE_TABLE_NAME +
                            " VALUES(null, $id, $height, ${System.currentTimeMillis()}, ${System.currentTimeMillis()}, ${System.currentTimeMillis()})"
                    db.execSQL(insertLogEntry)
                }
                innerCursor2.close()
            }
            innerCursor.close()
        }
        cursor.close()

        val deleteSetting = "DELETE FROM Settings WHERE setting_name = 'HEIGHT_SETTINGS'"
        db.execSQL(deleteSetting)

        val updateInch = "UPDATE " + ParameterMapper.DATABASE_TABLE_NAME +
                " SET unit = 'in' WHERE unit = 'inch'"
        db.execSQL(updateInch)

        db.execSQL(parameterTableQuery)
        db.execSQL("DROP TABLE IF EXISTS tempParameters")
    }
}