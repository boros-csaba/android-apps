package com.boroscsaba.commonlibrary.settings

import android.content.Context
import com.boroscsaba.commonlibrary.activities.ActivityBase
import com.boroscsaba.dataaccess.PersistentObject
import com.boroscsaba.dataaccess.RepositoryBaseBase

class SettingOption(override val context: Context): PersistentObject() {
    override var id : Int = 0
    override var createdDate: Long = 0
    override var modifiedDate: Long = 0
    override var guid = ""
    var settingName: String = ""
    var value: Double = 0.0
    var displayValue: String = ""
    var action: ((activity: ActivityBase) -> Unit)? = null

    override fun getRepository(): RepositoryBaseBase<PersistentObject>? {
        return null
    }
}