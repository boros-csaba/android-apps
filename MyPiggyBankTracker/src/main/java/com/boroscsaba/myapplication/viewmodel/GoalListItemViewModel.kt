package com.boroscsaba.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.boroscsaba.dataaccess.IObjectWithId
import com.boroscsaba.myapplication.logic.GoalLogic
import com.boroscsaba.myapplication.model.Goal

/**
* Created by boros on 3/2/2018.
*/
class GoalListItemViewModel(goal: Goal, private val application: Application) : IObjectWithId {
    override var id: Int = 0
    val title = MutableLiveData<String>()
    val percentage = MutableLiveData<Number>()
    val targetAmount = MutableLiveData<Number>()
    val currentAmount = MutableLiveData<Number>()
    val icon = MutableLiveData<Int>()
    val currencyCode = MutableLiveData<String>()
    val modifiedDate = MutableLiveData<Long>()
    val dueDate = MutableLiveData<Long>()

    init {
        id = goal.id
        title.value = goal.title
        percentage.value = (goal.transactions.sumByDouble { g -> g.amount } + goal.initialAmount) * 100 / goal.targetAmount
        targetAmount.value = goal.targetAmount
        currentAmount.value = goal.transactions.sumByDouble { g -> g.amount } + goal.initialAmount
        icon.value = goal.icon
        currencyCode.value = goal.currencyCode
        modifiedDate.value = goal.modifiedDate
        dueDate.value = goal.dueDate
    }

    fun hasPhotoIcon(): Boolean {
        return GoalLogic(application).hasPhotoIcon(icon.value!!)
    }

    fun getIconResourceId(): Int {
        return GoalLogic(application).getIconResourceId(icon.value!!)
    }
}