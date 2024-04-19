package com.boroscsaba.dataaccess

import android.content.Context

//todo move to common
abstract class EntityBase: IObjectWithId {
    abstract var createdDate: Long
    abstract var modifiedDate: Long
    abstract var guid: String

    open fun getNewTitle(context: Context): String { return "" }
    open fun getEditTitle(context: Context): String { return "" }
    open fun getEmptyStateTitle(context: Context): String { return "" }
    open fun getIcon(): Int? { return null }
    open fun getEmptyStateDescription(context: Context): String { return "" }
    open fun onlyOneObjectADay(): Boolean { return false }
    open fun getDailyUniqueValue(): Int { return 0 }
    open fun getEffectiveDate(): Long { return createdDate }

    open fun getPropertyEditors(): ArrayList<PropertyEditor<*>> {
        return ArrayList()
    }
}