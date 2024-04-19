package com.boroscsaba.myapplication.dataAccess

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.boroscsaba.dataaccess.IDbPatch
import java.io.File

/**
* Created by boros on 3/15/2018.
*/
class IconUriDbPatch(private val context: Context): IDbPatch {

    override fun applyPatch(db: SQLiteDatabase) {
        val cursor = db.query(GOALS_TABLE_NAME, arrayOf("id, icon_image"), "icon_image is not null", null, null, null, null)
        cursor.moveToFirst()
        if (cursor.count > 0) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val bitmap = cursor.getBlob(cursor.getColumnIndex("icon_image"))
                if (bitmap != null) {
                    val file = File(context.filesDir, "goal_icon_$id.png")
                    file.createNewFile()
                    file.writeBytes(bitmap)
                }
            } while (cursor.moveToNext())
        }
        cursor.close()

        updateGoalsTable(db)
        updateTransactionsTable(db)
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
                "goal_order INTEGER, " +
                "icon INTEGER, " +
                "modified_date DATE LONG);"
        db.execSQL(createNewGoalsTableQuery)
        val insertNewGoalsQuery = "INSERT INTO " + GOALS_TABLE_NAME +
                " SELECT id, title, target_amount, initial_amount, created_date, currency, goal_order, standard_icon, 0 " +
                "FROM tempGoals"
        db.execSQL(insertNewGoalsQuery)
        db.execSQL("DROP TABLE IF EXISTS tempGoals")
    }

    private fun updateTransactionsTable(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS tempTransactions")
        db.execSQL("ALTER TABLE $TRANSACTIONS_TABLE_NAME RENAME TO tempTransactions")
        val createNewTransactionsTableQuery = "CREATE TABLE IF NOT EXISTS " + TRANSACTIONS_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "amount DECIMAL(10,5) NOT NULL," +
                "title CHAR(100)," +
                "transaction_date DATE LONG," +
                "goal_id INTEGER," +
                "created_date DATE LONG," +
                "modified_date DATE LONG" +
                " REFERENCES " + GOALS_TABLE_NAME + "(id));"
        db.execSQL(createNewTransactionsTableQuery)
        val insertNewTransactionsQuery = "INSERT INTO " + TRANSACTIONS_TABLE_NAME +
                " SELECT id, amount, title, transaction_date, goal_id, 0, 0 " +
                "FROM tempTransactions"
        db.execSQL(insertNewTransactionsQuery)
        db.execSQL("DROP TABLE IF EXISTS tempTransactions")
    }

    companion object {
        private const val GOALS_TABLE_NAME = "Goals"
        private const val TRANSACTIONS_TABLE_NAME = "Transactions"
    }
}