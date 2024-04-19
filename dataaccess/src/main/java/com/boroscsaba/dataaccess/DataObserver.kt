package com.boroscsaba.dataaccess

import androidx.lifecycle.MutableLiveData
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper

/**
* Created by boros on 3/9/2018.
*/
class DataObserver<T>(handler: Handler, val liveData: MutableLiveData<T>, val uri: Uri, private val change: () -> T): ContentObserver(handler) {

    override fun onChange(selfChange: Boolean) {
        val result = change()
        Handler(Looper.getMainLooper()).post { liveData.value = result }
    }
}