package com.boroscsaba.myapplication.dataAccess

import android.content.Context
import com.boroscsaba.commonlibrary.LoggingHelper

import com.boroscsaba.myapplication.model.Transaction

/**
 * Created by Boros Csaba
 */

class TransactionRepository(private val context: Context) : RepositoryBase<Transaction>(context, com.boroscsaba.myapplication.model.Transaction::class.java, TransactionMapper(context)) {

    override fun upsert(persistentObject: Transaction, alreadySynced: Boolean): Int {
        if (persistentObject.id == 0) {
            LoggingHelper.logEvent(context, "New_Transaction")
        }
        return super.upsert(persistentObject, alreadySynced)
    }
}
