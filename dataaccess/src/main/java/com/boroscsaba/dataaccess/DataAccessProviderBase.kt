package com.boroscsaba.dataaccess

import android.content.Context

/**
* Created by boros on 3/4/2018.
*/
abstract class DataAccessProviderBase {
    private var dataAccess: DataAccess? = null

    fun getDataAccess(context: Context): DataAccess {
        val internalDataAccess = dataAccess
        return if (internalDataAccess != null) {
            internalDataAccess
        }
        else {
            val newDataAccess = getDataAccessInternal(context)
            dataAccess = newDataAccess
            newDataAccess
        }
    }

    abstract fun getDataAccessInternal(context: Context): DataAccess
}