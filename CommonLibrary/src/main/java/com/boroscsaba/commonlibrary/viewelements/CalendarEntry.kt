package com.boroscsaba.commonlibrary.viewelements

class CalendarEntry(val date: Long) {
    var action: (() -> Unit)? = null
}