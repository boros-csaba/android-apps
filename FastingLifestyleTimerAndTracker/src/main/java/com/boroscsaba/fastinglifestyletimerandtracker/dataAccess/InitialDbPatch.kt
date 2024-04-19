package com.boroscsaba.fastinglifestyletimerandtracker.dataAccess

import android.database.sqlite.SQLiteDatabase
import com.boroscsaba.dataaccess.BuildConfig

import com.boroscsaba.dataaccess.IDbPatch
import java.util.*

/**
 * Created by Boros Csaba
 */

internal class InitialDbPatch : IDbPatch {

    override fun applyPatch(db: SQLiteDatabase) {

        val fastsTableQuery = "CREATE TABLE IF NOT EXISTS " + FastMapper.DATABASE_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "created_date DATE LONG," +
                "modified_date DATE LONG, " +
                "start_date DATE LONG, " +
                "end_date DATE LONG, " +
                "target_hours INTEGER NOT NULL, " +
                "target_minutes INTEGER NOT NULL, " +
                "note VARCHAR(255));"
        db.execSQL(fastsTableQuery)

        if (BuildConfig.DEBUG) {
            val startDate = Calendar.getInstance()
            for (day in 0..30) {
                if (Math.random() > 0.3) {
                    startDate.timeInMillis = System.currentTimeMillis()
                    startDate.add(Calendar.HOUR, -24 * day)
                    val endDate = startDate.timeInMillis + 1000 * 60 * 60 * (Math.random() * 6)
                    val testDataScript = "INSERT INTO " + FastMapper.DATABASE_TABLE_NAME +
                            " VALUES (null, " +
                            startDate.timeInMillis + ", " +
                            startDate.timeInMillis + ", " +
                            startDate.timeInMillis + ", " +
                            endDate.toLong() + ", " +
                            (Math.random() * 6).toInt() + ", " +
                            "0, " +
                            "'')"
                    db.execSQL(testDataScript)
                }
            }
        }
    }
}
