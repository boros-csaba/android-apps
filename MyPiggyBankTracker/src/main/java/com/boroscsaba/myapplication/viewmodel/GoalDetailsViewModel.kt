package com.boroscsaba.myapplication.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.commonlibrary.activities.helpers.ViewModel
import com.boroscsaba.myapplication.logic.GoalLogic
import com.boroscsaba.myapplication.model.Goal

/**
* Created by boros on 3/4/2018.
*/
class GoalDetailsViewModel(application: Application) : ViewModel(application) {

    val goalSource = MutableLiveData<Goal>()

    var goalId = 0
    val title = Transformations.map(goalSource) { g -> g.title }
    val currentSum = Transformations.map(goalSource) { g -> g.initialAmount + g.transactions.sumByDouble { t -> t.amount } }
    val targetAmount = Transformations.map(goalSource) { g -> g.targetAmount }
    val currency = Transformations.map(goalSource) { g -> g.currency }
    val icon = Transformations.map(goalSource) { g -> g.icon }
    val percentage = Transformations.map(goalSource) { g -> Utils.round((g.transactions.sumByDouble { t -> t.amount } + g.initialAmount) * 100 / g.targetAmount, 2) }
    val transactions = Transformations.map(goalSource) { g -> ArrayList(g.transactions) }
    val completed = Transformations.map(goalSource) { g -> Utils.round(g.transactions.sumByDouble { t -> t.amount } + g.initialAmount, 2) >= g.targetAmount }
    val modifiedDate = Transformations.map(goalSource) { g -> g.modifiedDate }
    val dueDate = Transformations.map(goalSource) { g -> g.dueDate }

    override fun initialize(intent: Intent?) {
        val goalId = intent?.getIntExtra("GOAL_ID", 0) ?: return
        this.goalId = goalId
        GoalLogic(getApplication()).getGoalDetails(goalId, goalSource)
    }

    fun hasPhotoIcon(): Boolean {
        return GoalLogic(getApplication()).hasPhotoIcon(icon.value)
    }

    fun getIconResourceId(): Int {
        return GoalLogic(getApplication()).getIconResourceId(icon.value)
    }
}