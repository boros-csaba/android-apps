package com.boroscsaba.dataaccess

import android.os.AsyncTask

/**
* Created by boros on 2/5/2018.
*/
class AsyncTask : AsyncTask<() -> Unit, Any, Unit>() {

    override fun doInBackground(vararg function: () -> Unit) {
        function[0]()
    }
}