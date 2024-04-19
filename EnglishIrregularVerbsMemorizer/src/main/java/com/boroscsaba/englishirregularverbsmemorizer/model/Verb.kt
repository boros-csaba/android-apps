package com.boroscsaba.englishirregularverbsmemorizer.model

import com.boroscsaba.dataaccess.IObjectWithId

class Verb(override var id: Int, val infinitive: String, val simplePast: String, val pastParticiple: String): IObjectWithId {
    var isFilteredOut = false
}