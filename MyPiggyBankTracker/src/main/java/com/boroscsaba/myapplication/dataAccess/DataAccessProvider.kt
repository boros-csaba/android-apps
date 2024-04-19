package com.boroscsaba.myapplication.dataAccess

import android.content.Context

import com.boroscsaba.dataaccess.DataAccess

import android.content.Context.MODE_PRIVATE
import com.boroscsaba.commonlibrary.settings.SettingOptionDbPatch
import com.boroscsaba.commonlibrary.tutorial.TutorialDbPatch
import com.boroscsaba.dataaccess.DataAccessProviderBase
import com.boroscsaba.dataaccess.DataMapper
import com.boroscsaba.dataaccess.PersistentObject

/**
 * Created by Boros Csaba
 */

object DataAccessProvider: DataAccessProviderBase() {

    private const val DATABASE_NAME = "TransactionDB"

    override fun getDataAccessInternal(context: Context): DataAccess {
        val dbPatches = arrayOf(
                InitialDbPatch(),
                MultipleGoalsDbPatch(context.getSharedPreferences("MySharedPreferences", MODE_PRIVATE)),
                GoalCompletionDbPatch(),
                GoalOrderAndIconDbPatch(),
                RemoveGoalCompletionDbPatch(),
                IconUriDbPatch(context),
                TutorialDbPatch(),
                SettingOptionDbPatch(),
                DueDateDbPatch(),
                GuidDbPatch(),
                NotificationsDbPatch())
        val dataAccess = DataAccess(context, DATABASE_NAME, dbPatches)

        @Suppress("UNCHECKED_CAST")
        dataAccess.registerDataMapper(com.boroscsaba.myapplication.model.Transaction::class.java, TransactionMapper(context) as DataMapper<PersistentObject>)
        @Suppress("UNCHECKED_CAST")
        dataAccess.registerDataMapper(com.boroscsaba.myapplication.model.Goal::class.java, GoalMapper(context) as DataMapper<PersistentObject>)

        return dataAccess
    }
}
