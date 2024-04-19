package com.boroscsaba.myapplication.dataAccess

import android.database.sqlite.SQLiteDatabase

import com.boroscsaba.dataaccess.IDbPatch

/**
 * Created by Boros Csaba
 */

internal class InitialDbPatch : IDbPatch {

    override fun applyPatch(db: SQLiteDatabase) {
        val query = "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "amount DOUBLE NOT NULL," +
                "title CHAR(100)," +
                "transaction_date DATE LONG);"
        db.execSQL(query)
    }

    companion object {
        private const val DATABASE_TABLE_NAME = "Transactions"
    }
}
