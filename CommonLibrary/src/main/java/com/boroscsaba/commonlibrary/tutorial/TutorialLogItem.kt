package com.boroscsaba.commonlibrary.tutorial

import android.content.Context
import com.boroscsaba.dataaccess.PersistentObject
import com.boroscsaba.dataaccess.RepositoryBaseBase

class TutorialLogItem(override val context: Context): PersistentObject() {
    override var id : Int = 0
    override var createdDate: Long = 0
    override var modifiedDate: Long = 0
    override var guid = ""
    var title: String = ""

    override fun getRepository(): RepositoryBaseBase<PersistentObject>? {
        return null
    }
}