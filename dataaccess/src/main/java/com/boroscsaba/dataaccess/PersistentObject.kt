package com.boroscsaba.dataaccess

import android.content.Context

/**
 * Created by Boros Csaba
 */

abstract class PersistentObject: EntityBase() {



    abstract val context : Context

    abstract fun getRepository(): RepositoryBaseBase<PersistentObject>?




}


