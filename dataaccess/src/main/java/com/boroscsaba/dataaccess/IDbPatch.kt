package com.boroscsaba.dataaccess

import android.database.sqlite.SQLiteDatabase

/**
 * Created by Boros Csaba
 */

interface IDbPatch {
    fun applyPatch(db: SQLiteDatabase)
}
