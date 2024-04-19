package com.boroscsaba.myapplication.logic

import androidx.lifecycle.MutableLiveData
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import com.boroscsaba.commonlibrary.AsyncTask
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.dataaccess.DataObserverFactory
import com.boroscsaba.myapplication.R
import com.boroscsaba.myapplication.dataAccess.GoalMapper
import com.boroscsaba.myapplication.dataAccess.GoalRepository
import com.boroscsaba.myapplication.dataAccess.TransactionMapper
import com.boroscsaba.myapplication.dataAccess.TransactionRepository
import com.boroscsaba.myapplication.model.Goal


/**
* Created by boros on 3/3/2018.
*/
class GoalLogic(private val context: Context) {

    fun getActiveGoals(liveData: MutableLiveData<ArrayList<Goal>>) {
        DataObserverFactory<ArrayList<Goal>>(context).observe(GoalMapper(context).getUri(), liveData) { getActiveGoals() }
        AsyncTask().execute({
            val result = getActiveGoals()
            Handler(Looper.getMainLooper()).post { liveData.value = result }
        })
    }

    fun getActiveGoals(): ArrayList<Goal> {
        val goals = com.boroscsaba.myapplication.dataAccess.GoalRepository(context).getObjects(null, null, "goal_order")
        val transactions = TransactionRepository(context).getObjects(null, null, null)
        for (goal in goals) {
            goal.transactions = ArrayList(transactions.filter { t -> t.goalId == goal.id })
        }
        return ArrayList(goals.filter{ g -> g.targetAmount > Utils.round(g.initialAmount + g.transactions.sumByDouble { t -> t.amount }, 2)})
    }

    fun getGoalDetails(goalId: Int, liveData: MutableLiveData<Goal>) {
        DataObserverFactory<Goal>(context).observe(GoalMapper(context).getUri(goalId), liveData) { getGoalDetails(goalId) }
        DataObserverFactory<Goal>(context).observe(TransactionMapper(context).getUri(), liveData) { getGoalDetails(goalId) }
        AsyncTask().execute({
            val result = getGoalDetails(goalId)
            Handler(Looper.getMainLooper()).post { liveData.value = result }
        })
    }

    private fun getGoalDetails(goalId: Int): Goal {
        val goal = GoalRepository(context).getObjectById(goalId) ?: Goal(context)
        val transactions = TransactionRepository(context).getObjects("goal_id = ?", arrayOf(goal.id.toString()), "transaction_date desc")
        goal.transactions = transactions
        transactions.forEach { transaction -> transaction.goal = goal }
        return goal
    }

    fun getGoalForEdit(goalId: Int): Goal {
        var goal = GoalRepository(context).getObjectById(goalId)
        if (goal == null) {
            goal = Goal(context)
            goal.currencyCode = determineDefaultCurrency()
        }
        return goal
    }

    private fun determineDefaultCurrency() : String {
        val lastGoal = GoalRepository(context).getObjects(null, null, "id DESC LIMIT 1").firstOrNull()
        return lastGoal?.currencyCode ?: context.getString(R.string.default_currency)
    }

    fun hasPhotoIcon(iconId: Int?): Boolean {
        return iconId == 1000
    }

    fun getIconResourceId(iconId: Int?): Int {
        return when (iconId) {
            1 -> R.drawable.icon_home
            2 -> R.drawable.icon_car
            3 -> R.drawable.icon_university
            4 -> R.drawable.icon_travel
            5 -> R.drawable.icon_devices
            6 -> R.drawable.icon_gift
            else -> R.drawable.icon_none
        }
    }

    fun saveGoal(goalId: Int, title: String, initialAmount: Double, targetAmount: Double, currencyCode: String, iconId: Int, photoIcon: Bitmap?, dueDate: Long, notificationEnabled: Boolean) {
        val goalRepository = GoalRepository(context)
        val goal = goalRepository.getObjectById(goalId) ?: Goal(context)
        goal.title = title
        goal.initialAmount = initialAmount
        goal.targetAmount = targetAmount
        goal.currencyCode = currencyCode
        goal.icon = iconId
        goal.dueDate = dueDate
        goal.notificationEnabled = notificationEnabled
        goalRepository.upsert(goal, false)
        if (photoIcon != null) {
            val stream = context.contentResolver.openOutputStream(GoalMapper(context).getImageUri(goal.id, goal.modifiedDate)) ?: return
            photoIcon.compress(Bitmap.CompressFormat.WEBP, 50, stream)
            stream.close()
            goalRepository.syncUpdatedImages(goal)
        }
    }

    fun deleteGoal(goalId: Int) {
        AsyncTask().execute({
            val transactionRepository = TransactionRepository(context)
            val transactions = transactionRepository.getObjects("goal_Id = ?", arrayOf(goalId.toString()), null)
            transactions.forEach { t -> transactionRepository.delete(t.id, false) }
            GoalRepository(context).delete(goalId, false)
        })
    }

    fun saveGoalOrder(orderedGoalIds: List<Int>) {
        AsyncTask().execute({
            val goals = getActiveGoals()
            val goalRepository = GoalRepository(context)
            for (i in goals.indices) {
                val goal = goals.first { g -> g.id == orderedGoalIds[i] }
                goal.goalOrder = i + 1
                goalRepository.upsert(goal, false)
            }
        })
    }

    fun getGoalsForOverview(liveData: MutableLiveData<ArrayList<Goal>>) {
        DataObserverFactory<ArrayList<Goal>>(context).observe(GoalMapper(context).getUri(), liveData) { getGoalsForOverview() }
        AsyncTask().execute({
            val result = getGoalsForOverview()
            Handler(Looper.getMainLooper()).post { liveData.value = result }
        })
    }

    private fun getGoalsForOverview(): ArrayList<Goal> {
        val goals = GoalRepository(context).getObjects(null, null, "goal_order")
        val transactions = TransactionRepository(context).getObjects(null, null, null)
        for (goal in goals) {
            goal.transactions = ArrayList(transactions.filter { t -> t.goalId == goal.id })
        }
        return goals
    }

    fun getGoalForWidget(goalId: Int): Goal? {
        val goal = GoalRepository(context).getObjectById(goalId) ?: return null
        val transactions = TransactionRepository(context).getObjects("goal_id = ?", arrayOf(goal.id.toString()), "transaction_date")
        goal.transactions = transactions
        return goal
    }
}