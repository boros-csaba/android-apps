package com.boroscsaba.myapplication.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.commonlibrary.activities.helpers.ViewModel
import com.boroscsaba.myapplication.logic.GoalLogic

/**
* Created by boros on 3/5/2018.
*/
class OverviewViewModel(application: Application) : ViewModel(application) {

    val goalsSource = MutableLiveData<ArrayList<com.boroscsaba.myapplication.model.Goal>>()

    val selectedCurrency = MutableLiveData<String>()
    val usedCurrencies = Transformations.map(goalsSource) { gs -> gs.map { g -> g.currencyCode }.distinct() }
    val completedGoals = Transformations.map(goalsSource) { gs -> gs.filter { g -> g.targetAmount <= Utils.round(g.initialAmount + g.transactions.sumByDouble { t -> t.amount }, 2) }.map { g -> GoalListItemViewModel(g, application) } }

    override fun initialize(intent: Intent?) {
        GoalLogic(getApplication()).getGoalsForOverview(goalsSource)
    }
}