package com.boroscsaba.myapplication.dataAccess

import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase

import com.boroscsaba.dataaccess.IDbPatch

/**
 * Created by Boros Csaba
 */

internal class MultipleGoalsDbPatch(private val sharedPreferences: SharedPreferences) : IDbPatch {

    override fun applyPatch(db: SQLiteDatabase) {
        createGoalsTable(db)
        val transactionsCount = getTransactionsCount(db)

        var defaultGoalId = 0
        if (transactionsCount > 0) {
            defaultGoalId = createDefaultGoal(db)
        }
        updateTransactionsTable(db, defaultGoalId)
    }

    private fun createGoalsTable(db: SQLiteDatabase) {
        val createGoalsTableQuery = "CREATE TABLE IF NOT EXISTS " + GOALS_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title CHAR(100)," +
                "target_amount DECIMAL(10,5) NOT NULL," +
                "initial_amount DECIMAL(10,5) NOT NULL," +
                "created_date DATE LONG, " +
                "currency CHAR(10) NOT NULL);"
        db.execSQL(createGoalsTableQuery)
    }

    private fun getTransactionsCount(db: SQLiteDatabase): Int {
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TRANSACTIONS_TABLE_NAME", null)
        cursor.moveToFirst()
        val transactionsCount = cursor.getInt(0)
        cursor.close()
        return transactionsCount
    }

    private fun createDefaultGoal(db: SQLiteDatabase): Int {
        val goalTitle = sharedPreferences.getString("goalTitle", "First goal")
        val currency = sharedPreferences.getString("currencyKey", "USD")
        val targetAmountLong = sharedPreferences.getLong("totalCost", 0)
        val targetAmount = java.lang.Double.longBitsToDouble(targetAmountLong)
        val startingAmountLong = sharedPreferences.getLong("startingAmount", 0)
        val startingAmount = java.lang.Double.longBitsToDouble(startingAmountLong)

        val createFirstGoalQuery = "INSERT INTO " + GOALS_TABLE_NAME + " VALUES (" +
                "NULL," +
                "'" + goalTitle + "'," +
                targetAmount + "," +
                startingAmount + "," +
                "datetime()," +
                "'" + currency + "');"
        db.execSQL(createFirstGoalQuery)

        val cursor = db.rawQuery("SELECT id FROM $GOALS_TABLE_NAME", null)
        cursor.moveToFirst()
        val defaultGoalId = cursor.getInt(0)
        cursor.close()
        return defaultGoalId
    }

    private fun updateTransactionsTable(db: SQLiteDatabase, defaultGoalId: Int) {
        db.execSQL("DROP TABLE IF EXISTS tempTransactions")
        db.execSQL("ALTER TABLE $TRANSACTIONS_TABLE_NAME RENAME TO tempTransactions")
        val createNewTransactionsTableQuery = "CREATE TABLE IF NOT EXISTS " + TRANSACTIONS_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "amount DECIMAL(10,5) NOT NULL," +
                "title CHAR(100)," +
                "transaction_date DATE LONG," +
                "goal_id INTEGER " +
                " REFERENCES " + GOALS_TABLE_NAME + "(id));"
        db.execSQL(createNewTransactionsTableQuery)
        val insertNewTransactionsQuery = "INSERT INTO " + TRANSACTIONS_TABLE_NAME +
                " SELECT id, amount, title, transaction_date, " + defaultGoalId + " as goal_id " +
                "FROM tempTransactions"
        db.execSQL(insertNewTransactionsQuery)
        db.execSQL("DROP TABLE IF EXISTS tempTransactions")
    }

    companion object {

        private const val GOALS_TABLE_NAME = "Goals"
        private const val TRANSACTIONS_TABLE_NAME = "Transactions"
    }
}
