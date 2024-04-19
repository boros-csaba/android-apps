package com.boroscsaba.bodymeasurementsandweightlosstracker.dataAccess

import android.content.Context

import com.boroscsaba.dataaccess.DataMapper
import com.boroscsaba.dataaccess.PersistentObject
import com.boroscsaba.dataaccess.RepositoryBaseBase

/**
 * Created by Boros Csaba
 */

abstract class RepositoryBase<T : PersistentObject>(context: Context, classType: Class<*>, mapper: DataMapper<T>)
    : RepositoryBaseBase<T>(context, DataAccessProvider.getDataAccess(context), classType, mapper)
