package com.boroscsaba.myapplication.dataAccess

import android.content.Context
import com.boroscsaba.commonlibrary.LoggingHelper

import com.boroscsaba.myapplication.model.Goal

/**
 * Created by Boros Csaba
 */

class GoalRepository(private val context: Context) : RepositoryBase<Goal>(context, com.boroscsaba.myapplication.model.Goal::class.java, GoalMapper(context)) {

    override fun upsert(persistentObject: Goal, alreadySynced: Boolean) : Int {
        if (persistentObject.id == 0) {
            LoggingHelper.logEvent(context, "New_Goal","Title", persistentObject.title)
        }
        return super.upsert(persistentObject, alreadySynced)
    }

    override fun getImagesInBase64(persistentObject: Goal): ArrayList<String> {
        val result = ArrayList<String>()
        if (persistentObject.icon == 1000) {
            result.add(GoalMapper(context).getImageBase64String(persistentObject.id, persistentObject.modifiedDate))
        }
        return result
    }
}
