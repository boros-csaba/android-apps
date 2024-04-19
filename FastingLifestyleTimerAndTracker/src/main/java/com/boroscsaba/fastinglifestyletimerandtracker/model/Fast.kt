package com.boroscsaba.fastinglifestyletimerandtracker.model

import android.content.Context
import com.boroscsaba.dataaccess.IObjectWithId
import com.boroscsaba.dataaccess.PersistentObject
import com.boroscsaba.dataaccess.RepositoryBaseBase
import com.boroscsaba.fastinglifestyletimerandtracker.dataAccess.FastRepository

/**
 * Created by Boros Csaba
 */

class Fast(override val context: Context) : PersistentObject(), IObjectWithId {
    override var id : Int = 0
    override var createdDate: Long = 0
    override var modifiedDate: Long = 0
    override var guid = ""
    var startDate: Long = 0
    var endDate: Long = 0
    var targetHours = 0
    var targetMinutes = 0
    var note = ""

    override fun getRepository(): RepositoryBaseBase<PersistentObject>? {
        @Suppress("UNCHECKED_CAST")
        return FastRepository(context) as RepositoryBaseBase<PersistentObject>
    }
}
