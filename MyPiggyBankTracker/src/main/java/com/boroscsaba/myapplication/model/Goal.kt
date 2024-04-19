package com.boroscsaba.myapplication.model

import android.content.Context
import com.boroscsaba.dataaccess.IObjectWithId
import com.boroscsaba.commonlibrary.viewelements.currency.Currency
import com.boroscsaba.commonlibrary.viewelements.currency.CurrencyManager

import com.boroscsaba.dataaccess.PersistentObject
import com.boroscsaba.dataaccess.RepositoryBaseBase
import com.boroscsaba.myapplication.dataAccess.GoalRepository

/**
 * Created by Boros Csaba
 */

class Goal(override val context: Context) : PersistentObject(), IObjectWithId {
    override var id: Int = 0
    override var createdDate: Long = 0
    override var modifiedDate: Long = 0
    override var guid = ""
    var title: String = ""
    var targetAmount: Double = 0.toDouble()
    var initialAmount: Double = 0.toDouble()
    var currencyCode: String = ""
    var goalOrder: Int = 0
    var icon: Int = 0
    var dueDate: Long = 0
    var notificationEnabled = true

    var transactions = ArrayList<Transaction>()

    val currency: Currency get() = CurrencyManager.getCurrency(currencyCode)

    override fun getRepository(): RepositoryBaseBase<PersistentObject>? {
        @Suppress("UNCHECKED_CAST")
        return GoalRepository(context) as RepositoryBaseBase<PersistentObject>
    }

    companion object {
        const val MAX_IMAGE_SIZE = 256
    }
}
