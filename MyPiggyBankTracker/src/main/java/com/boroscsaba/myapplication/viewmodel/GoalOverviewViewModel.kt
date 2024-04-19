package com.boroscsaba.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.boroscsaba.dataaccess.IObjectWithId
import com.boroscsaba.myapplication.logic.GoalLogic
import com.boroscsaba.myapplication.model.Goal

/**
* Created by boros on 3/11/2018.
*/
class GoalOverviewViewModel(goal: Goal, private val application: Application) : IObjectWithId {
    override var id: Int = 0
    val title = MutableLiveData<String>()
    val currency = MutableLiveData<String>()
    val percentage = MutableLiveData<Number>()
    val icon = MutableLiveData<Int>()
    val currencyCode = MutableLiveData<String>()
    val modifiedDate = MutableLiveData<Long>()

    init {
        id = goal.id
        title.value = goal.title
        currency.value = goal.currencyCode
        percentage.value = (goal.transactions.sumByDouble { g -> g.amount } + goal.initialAmount) * 100 / goal.targetAmount
        icon.value = goal.icon
        currencyCode.value = goal.currencyCode
        modifiedDate.value = goal.modifiedDate
    }

    fun hasPhotoIcon(): Boolean {
        return GoalLogic(application).hasPhotoIcon(icon.value!!)
    }

    fun getIconResourceId(): Int {
        return GoalLogic(application).getIconResourceId(icon.value!!)
    }
}