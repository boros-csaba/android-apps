package com.boroscsaba.dataaccess

import androidx.lifecycle.MutableLiveData
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread

/**
* Created by boros on 3/9/2018.
*/
class DataObserverFactory<T>(private val context: Context) {

    fun observe(uri: Uri, liveData: MutableLiveData<T>, change: () -> T) {
        val thread = HandlerThread("MyHandlerThread")
        thread.start()
        val handler = Handler(thread.looper)
        if (!registeredObservers.any { registration -> registration.uri == uri && registration.liveData == liveData }) {
            val observer = DataObserver(handler, liveData, uri, change)
            context.contentResolver.registerContentObserver(uri, true, observer)
            registeredObservers.add(ObserverRegistration(uri, liveData))
        }
    }

    class ObserverRegistration(val uri: Uri, val liveData: MutableLiveData<*>)

    companion object {
        private val registeredObservers = ArrayList<ObserverRegistration>()
    }
}