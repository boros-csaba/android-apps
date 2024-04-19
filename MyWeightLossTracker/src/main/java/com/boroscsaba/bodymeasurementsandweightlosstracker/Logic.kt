package com.boroscsaba.myweightlosstracker

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.boroscsaba.myweightlosstracker.dataAccess.MeasurementMapper
import com.boroscsaba.myweightlosstracker.dataAccess.MeasurementRepository
import com.boroscsaba.myweightlosstracker.dataAccess.ParameterMapper
import com.boroscsaba.myweightlosstracker.dataAccess.ParameterRepository
import com.boroscsaba.myweightlosstracker.model.Parameter
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.AsyncTask
import com.boroscsaba.commonlibrary.tutorial.TutorialHelper
import com.boroscsaba.commonlibrary.tutorial.TutorialsEnum
import com.boroscsaba.dataaccess.DataObserverFactory

class Logic(private val context: Context) {

    fun getParameters(liveData: MutableLiveData<ArrayList<Parameter>>) {
        DataObserverFactory<ArrayList<Parameter>>(context).observe(ParameterMapper(context).getUri(), liveData) { getParameters() }
        DataObserverFactory<ArrayList<Parameter>>(context).observe(MeasurementMapper(context).getUri(), liveData) { getParameters() }
        AsyncTask().execute({
            val result = getParameters()
            Handler(Looper.getMainLooper()).post { liveData.value = result }
        })
    }

    private fun getParameters(): ArrayList<Parameter> {
        val measurementRepository = MeasurementRepository(context)
        val parameterRepository = ParameterRepository(context)
        val parameters = parameterRepository.getObjects(null, null, null)
        parameters.forEach { parameter ->
            parameter.measurements.addAll(measurementRepository.getObjects("parameter_id = ?", arrayOf(parameter.id.toString()), "log_date desc"))
            parameter.measurements.forEach { measurement ->
                measurement.parameter = parameter
            }
        }

        val tutorialHelper = TutorialHelper(context.applicationContext as ApplicationBase)
        if (!tutorialHelper.isTutorialCompleted(TutorialsEnum.APP_FIRST_START) && parameters.isEmpty()) {
            val weight = Parameter(context)
            weight.name = "Weight"
            weight.unit = "lbs"
            parameterRepository.upsert(weight, false)
            val hips = Parameter(context)
            hips.name = "Hips"
            hips.unit = "in"
            parameterRepository.upsert(hips, false)
            val waist = Parameter(context)
            waist.name = "Waist"
            waist.unit = "in"
            parameterRepository.upsert(waist, false)
            val bust = Parameter(context)
            bust.name = "Bust"
            bust.unit = "in"
            parameterRepository.upsert(bust, false)
            tutorialHelper.completeTutorial(TutorialsEnum.APP_FIRST_START)
        }

        return parameters
    }
}