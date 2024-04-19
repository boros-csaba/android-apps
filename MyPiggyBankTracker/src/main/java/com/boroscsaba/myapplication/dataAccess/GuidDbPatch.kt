package com.boroscsaba.myapplication.dataAccess

import android.database.sqlite.SQLiteDatabase
import com.boroscsaba.dataaccess.IDbPatch

class GuidDbPatch: IDbPatch {

    override fun applyPatch(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS tempGoals")
        db.execSQL("ALTER TABLE $GOALS_TABLE_NAME RENAME TO tempGoals")
        val createNewGoalsTableQuery = "CREATE TABLE IF NOT EXISTS " + GOALS_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title CHAR(100)," +
                "target_amount DECIMAL(10,5) NOT NULL," +
                "initial_amount DECIMAL(10,5) NOT NULL," +
                "created_date DATE LONG, " +
                "currency CHAR(10) NOT NULL, " +
                "goal_order INTEGER, " +
                "icon INTEGER, " +
                "modified_date DATE LONG, " +
                "due_date DATE LONG," +
                "guid char(50));"
        db.execSQL(createNewGoalsTableQuery)
        val insertNewGoalsQuery = "INSERT INTO " + GOALS_TABLE_NAME +
                " SELECT id, title, target_amount, initial_amount, created_date, currency, goal_order, icon, modified_date, due_date, '' " +
                "FROM tempGoals"
        db.execSQL(insertNewGoalsQuery)
        db.execSQL("DROP TABLE IF EXISTS tempGoals")

        db.execSQL("DROP TABLE IF EXISTS tempTransactions")
        db.execSQL("ALTER TABLE $TRANSACTIONS_TABLE_NAME RENAME TO tempTransactions")
        val createNewTransactionsTableQuery = "CREATE TABLE IF NOT EXISTS " + TRANSACTIONS_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "amount DECIMAL(10,5) NOT NULL," +
                "title CHAR(100)," +
                "transaction_date DATE LONG," +
                "goal_id INTEGER," +
                "created_date DATE LONG," +
                "modified_date DATE LONG," +
                "guid char(50)" +
                " REFERENCES " + GOALS_TABLE_NAME + "(id));"
        db.execSQL(createNewTransactionsTableQuery)
        val insertNewTransactionsQuery = "INSERT INTO " + TRANSACTIONS_TABLE_NAME +
                " SELECT id, amount, title, transaction_date, goal_id, created_date, modified_date, '' " +
                "FROM tempTransactions"
        db.execSQL(insertNewTransactionsQuery)
        db.execSQL("DROP TABLE IF EXISTS tempTransactions")
    }

    companion object {
        private const val GOALS_TABLE_NAME = "Goals"
        private const val TRANSACTIONS_TABLE_NAME = "Transactions"
    }
}