package com.boroscsaba.commonlibrary.helpers

import com.boroscsaba.dataaccess.EntityBase

class EntityHelper {
    companion object {

        @Suppress("UNCHECKED_CAST")
        fun <T: EntityBase> getNew(classType: Class<T>): T {
            return classType.constructors.first().newInstance() as T
        }
    }
}