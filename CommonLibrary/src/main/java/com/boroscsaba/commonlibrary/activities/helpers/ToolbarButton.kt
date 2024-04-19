package com.boroscsaba.commonlibrary.activities.helpers

/**
* Created by boros on 3/24/2018.
*/
class ToolbarButton(val titleResource: Int, val iconResource: Int?) {
    var isActive = true
    var action: (() -> Boolean)? = null
    var executed: Boolean = false
}