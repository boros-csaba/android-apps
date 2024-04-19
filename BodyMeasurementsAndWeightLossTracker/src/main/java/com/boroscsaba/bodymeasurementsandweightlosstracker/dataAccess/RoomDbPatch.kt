package com.boroscsaba.bodymeasurementsandweightlosstracker.dataAccess

import android.database.sqlite.SQLiteDatabase
import com.boroscsaba.dataaccess.IDbPatch

class RoomDbPatch: IDbPatch {
    override fun applyPatch(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS tempParameter")
        db.execSQL("ALTER TABLE Parameter RENAME TO tempParameter")
        db.execSQL("CREATE TABLE IF NOT EXISTS Parameter (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "created_date INTEGER NOT NULL, " +
                "modified_date INTEGER NOT NULL, " +
                "guid TEXT NOT NULL, " +
                "name TEXT NOT NULL, " +
                "goal_value REAL NOT NULL, " +
                "unit TEXT NOT NULL)")
        db.execSQL("INSERT INTO Parameter " +
                "SELECT id, created_date, modified_date, guid, name, goal_value, unit " +
                "FROM tempParameter")
        db.execSQL("DROP TABLE IF EXISTS tempParameter")

        db.execSQL("DROP TABLE IF EXISTS tempMeasurements")
        db.execSQL("ALTER TABLE Measurements RENAME TO tempMeasurements")
        db.execSQL("CREATE TABLE IF NOT EXISTS Measurements (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "created_date INTEGER NOT NULL, " +
                "modified_date INTEGER NOT NULL, " +
                "guid TEXT NOT NULL, " +
                "value REAL NOT NULL, " +
                "parameter_id INTEGER NOT NULL, " +
                "log_date INTEGER NOT NULL, " +
                "foreign key(parameter_id) references Parameter(id) on delete cascade)")
        db.execSQL("INSERT INTO Measurements " +
                "SELECT id, created_date, modified_date, guid, value, parameter_id, log_date " +
                "FROM tempMeasurements")
        db.execSQL("DROP TABLE IF EXISTS tempMeasurements")
    }
}