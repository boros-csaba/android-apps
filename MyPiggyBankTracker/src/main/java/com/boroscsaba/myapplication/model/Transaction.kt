package com.boroscsaba.myapplication.model

import android.content.Context
import com.boroscsaba.dataaccess.PersistentObject
import com.boroscsaba.dataaccess.RepositoryBaseBase
import com.boroscsaba.myapplication.dataAccess.TransactionRepository

/**
 * Created by Boros Csaba
 */
class Transaction(override val context: Context) : PersistentObject() {
    override var id: Int = 0
    override var createdDate: Long = 0
    override var modifiedDate: Long = 0
    override var guid = ""
    var amount: Double = 0.toDouble()
    var title: String = ""
    var transactionDate: Long = 0
    var goalId: Int = 0
    var goal: Goal? = null

    override fun getRepository(): RepositoryBaseBase<PersistentObject>? {
        @Suppress("UNCHECKED_CAST")
        return TransactionRepository(context) as RepositoryBaseBase<PersistentObject>
    }
}
