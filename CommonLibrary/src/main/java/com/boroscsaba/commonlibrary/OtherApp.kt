package com.boroscsaba.commonlibrary

import com.boroscsaba.dataaccess.IObjectWithId

class OtherApp: IObjectWithId {
    override var id: Int = 0
    var title: String = ""
    var icon: Int = 0
    var packageName: String = ""
    var installed = false
}