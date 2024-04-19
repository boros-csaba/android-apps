package com.boroscsaba.myapplication.dataAccess

import android.database.sqlite.SQLiteDatabase

import com.boroscsaba.dataaccess.IDbPatch

/**
 * Created by Boros Csaba
 */

class GoalCompletionDbPatch : IDbPatch {

    override fun applyPatch(db: SQLiteDatabase) {
        updateGoalsTable(db)
    }

    private fun updateGoalsTable(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS tempGoals")
        db.execSQL("ALTER TABLE $GOALS_TABLE_NAME RENAME TO tempGoals")
        val createNewGoalsTableQuery = "CREATE TABLE IF NOT EXISTS " + GOALS_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title CHAR(100)," +
                "target_amount DECIMAL(10,5) NOT NULL," +
                "initial_amount DECIMAL(10,5) NOT NULL," +
                "created_date DATE LONG, " +
                "currency CHAR(10) NOT NULL, " +
                "completed INTEGER);"
        db.execSQL(createNewGoalsTableQuery)
        val insertNewGoalsQuery = "INSERT INTO " + GOALS_TABLE_NAME +
                " SELECT id, title, target_amount, initial_amount, created_date, currency, 0 " +
                "FROM tempGoals"
        db.execSQL(insertNewGoalsQuery)
        db.execSQL("DROP TABLE IF EXISTS tempGoals")
    }

    companion object {

        private const val GOALS_TABLE_NAME = "Goals"
    }
}
