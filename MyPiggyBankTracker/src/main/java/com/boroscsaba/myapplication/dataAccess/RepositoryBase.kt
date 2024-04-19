package com.boroscsaba.myapplication.dataAccess

import android.content.Context
import com.boroscsaba.commonlibrary.cloudsync.CloudSyncRepository

import com.boroscsaba.dataaccess.DataMapper
import com.boroscsaba.dataaccess.PersistentObject

/**
 * Created by Boros Csaba
 */

abstract class RepositoryBase<T : PersistentObject>(context: Context, classType: Class<*>, mapper: DataMapper<T>)
    : CloudSyncRepository<T>(context, DataAccessProvider.getDataAccess(context), classType, mapper)
