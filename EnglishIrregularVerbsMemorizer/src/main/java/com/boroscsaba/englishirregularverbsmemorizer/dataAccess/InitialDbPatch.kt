package com.boroscsaba.englishirregularverbsmemorizer.dataAccess

import android.database.sqlite.SQLiteDatabase

import com.boroscsaba.dataaccess.IDbPatch

/**
 * Created by Boros Csaba
 */

internal class InitialDbPatch : IDbPatch {

    override fun applyPatch(db: SQLiteDatabase) {

        val answerTableQuery = "CREATE TABLE IF NOT EXISTS " + AnswerMapper.DATABASE_TABLE_NAME + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "created_date DATE LONG, " +
                "modified_date DATE LONG, " +
                "guid VARCHAR(255), " +
                "verb_id INTEGER NOT NULL, " +
                "correct_answers INTEGER NOT NULL, " +
                "wrong_answers INTEGER NOT NULL);"
        db.execSQL(answerTableQuery)

        val dailyResultTableQuery = "CREATE TABLE IF NOT EXISTS " + DailyResultMapper.DATABASE_TABLE_NAME + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "created_date DATE LONG, " +
                "modified_date DATE LONG, " +
                "guid VARCHAR(255), " +
                "day DATE LONG, " +
                "correct_guesses INTEGER NOT NULL, " +
                "missed_guesses INTEGER NOT NULL, " +
                "goal_met INTEGER NOT NULL);"
        db.execSQL(dailyResultTableQuery)
    }
}