package com.boroscsaba.myapplication.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.boroscsaba.commonlibrary.activities.helpers.ViewModel
import com.boroscsaba.myapplication.logic.GoalLogic
import com.boroscsaba.myapplication.model.Goal

/**
* Created by boros on 3/2/2018.
*/
class GoalsListViewModel(application: Application) : ViewModel(application) {

    private val goalsSource = MutableLiveData<ArrayList<Goal>>()

    var goals = Transformations.map(goalsSource) { gs -> ArrayList(gs.map { g -> GoalListItemViewModel(g, getApplication()) }) }

    override fun initialize(intent: Intent?) {
        GoalLogic(getApplication()).getActiveGoals(goalsSource)
    }

    fun undoReordering() {
        goalsSource.value = goalsSource.value
    }

    fun saveGoalOrder() {
        GoalLogic(getApplication()).saveGoalOrder(goals.value!!.map{ g -> g.id })
    }
}