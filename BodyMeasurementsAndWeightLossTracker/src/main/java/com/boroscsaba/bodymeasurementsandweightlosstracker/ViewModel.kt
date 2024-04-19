package com.boroscsaba.bodymeasurementsandweightlosstracker

import android.app.Application
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.boroscsaba.bodymeasurementsandweightlosstracker.model.AppDatabase
import com.boroscsaba.bodymeasurementsandweightlosstracker.model.Measurement
import com.boroscsaba.bodymeasurementsandweightlosstracker.model.Parameter
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.AsyncTask
import com.boroscsaba.commonlibrary.activities.helpers.ViewModel
import com.boroscsaba.commonlibrary.tutorial.TutorialHelper
import com.boroscsaba.commonlibrary.tutorial.TutorialsEnum

class ViewModel(application: Application): ViewModel(application) {

    val parameters: LiveData<ArrayList<Parameter>>
    val measurements: LiveData<ArrayList<Measurement>>

    init {
        val database = AppDatabase.getDatabase(getApplication())
        parameters = Transformations.map(database.parameterDao().getAllAsync()) { x -> ArrayList(x) }
        measurements = Transformations.map(database.measurementDao().getAllAsync()) { x -> ArrayList(x) }
    }

    override fun initialize(intent: Intent?) {
        AsyncTask().execute({
            val tutorialHelper = TutorialHelper(getApplication() as ApplicationBase)
            if (!tutorialHelper.isTutorialCompleted(TutorialsEnum.APP_FIRST_START) && parameters.value?.isEmpty() != true) {
                val database = AppDatabase.getDatabase(getApplication())
                val weight = Parameter()
                weight.name = "Weight"
                weight.unit = "lbs"
                database.parameterDao().insert(weight)
                val hips = Parameter()
                hips.name = "Hips"
                hips.unit = "in"
                database.parameterDao().insert(hips)
                val waist = Parameter()
                waist.name = "Waist"
                waist.unit = "in"
                database.parameterDao().insert(waist)
                val bust = Parameter()
                bust.name = "Bust"
                bust.unit = "in"
                database.parameterDao().insert(bust)
                tutorialHelper.completeTutorial(TutorialsEnum.APP_FIRST_START)
            }})
    }
}