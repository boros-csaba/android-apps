package com.boroscsaba.fastinglifestyletimerandtracker.logic

import android.app.Application
import androidx.lifecycle.MutableLiveData
import android.os.Handler
import android.os.Looper
import com.boroscsaba.commonlibrary.AsyncTask
import com.boroscsaba.dataaccess.DataObserverFactory
import com.boroscsaba.fastinglifestyletimerandtracker.dataAccess.FastMapper
import com.boroscsaba.fastinglifestyletimerandtracker.dataAccess.FastRepository
import com.boroscsaba.fastinglifestyletimerandtracker.model.Fast

class FastLogic(private val application: Application) {

    fun saveFast(fastId: Int, startDate: Long, endDate: Long, targetHours: Int, targetMinutes: Int) {
        val fastRepository = FastRepository(application)
        val fast = fastRepository.getObjectById(fastId) ?: Fast(application)
        fast.startDate = startDate
        fast.endDate = endDate
        fast.targetHours = targetHours
        fast.targetMinutes = targetMinutes
        fastRepository.upsert(fast, false)
    }

    fun deleteFast(fastId: Int) {
        FastRepository(application).delete(fastId, false)
    }

    fun getFastHistory(liveData: MutableLiveData<ArrayList<Fast>>) {
        DataObserverFactory<ArrayList<Fast>>(application).observe(FastMapper(application).getUri(), liveData) { getFastHistory() }
       AsyncTask().execute({
            val result = getFastHistory()
            Handler(Looper.getMainLooper()).post{ liveData.value = result }
        })
    }

    private fun getFastHistory(): ArrayList<Fast> {
        val fasts = FastRepository(application).getObjects(null, null, null)
        return ArrayList(fasts.sortedByDescending{ f -> f.startDate })
    }
}