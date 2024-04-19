package com.boroscsaba.myweightlosstracker

import android.app.Application
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.boroscsaba.myweightlosstracker.model.Parameter
import com.boroscsaba.commonlibrary.activities.helpers.ViewModel

class ViewModel(application: Application): ViewModel(application) {

    val parameters = MutableLiveData<ArrayList<Parameter>>()
    val measurements = Transformations.map(parameters) { parameters -> ArrayList(parameters.flatMap { parameter -> parameter.measurements }) }

    override fun initialize(intent: Intent?) {
        Logic(getApplication()).getParameters(parameters)
    }
}