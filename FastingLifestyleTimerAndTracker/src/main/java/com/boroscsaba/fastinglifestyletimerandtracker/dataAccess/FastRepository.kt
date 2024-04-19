package com.boroscsaba.fastinglifestyletimerandtracker.dataAccess

import android.content.Context
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.commonlibrary.helpers.SingletonHolder
import com.boroscsaba.fastinglifestyletimerandtracker.model.Fast

/**
 * Created by Boros Csaba
 */
class FastRepository(private val context: Context) : RepositoryBase<Fast>(context, Fast::class.java, FastMapper(context)) {

    override fun upsert(persistentObject: Fast, alreadySynced: Boolean) : Int {
        if (persistentObject.id == 0) {
            LoggingHelper.logEvent(context, "New_fast_started")
        }
        return super.upsert(persistentObject, alreadySynced)
    }

    companion object : SingletonHolder<FastRepository, Context>(::FastRepository)
}