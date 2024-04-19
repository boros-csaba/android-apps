package com.boroscsaba.commonlibrary.activities.helpers

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel

abstract class ViewModel(application: Application): AndroidViewModel(application) {
    abstract fun initialize(intent: Intent?)
}