package com.boroscsaba.myapplication.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import android.graphics.Bitmap
import android.net.Uri
import com.boroscsaba.commonlibrary.activities.helpers.ViewModel
import com.boroscsaba.myapplication.logic.GoalLogic

/**
* Created by boros on 3/5/2018.
*/
class GoalEditViewModel(application: Application) : ViewModel(application) {

    var goalId = 0
    val title = MutableLiveData<String>()
    val initialAmount = MutableLiveData<Double>()
    val targetAmount = MutableLiveData<Double>()
    val currencyCode = MutableLiveData<String>()
    val iconId = MutableLiveData<Int>()
    val photoIcon = MutableLiveData<Bitmap>()
    val iconUri = MutableLiveData<Uri>()
    var dueDate = MutableLiveData<Long>()
    var modifiedDate: Long = 0
    var notificationEnabled = MutableLiveData<Boolean>()

    override fun initialize(intent: Intent?) {
        val goalId = intent?.getIntExtra("GOAL_ID", 0) ?: return
        this.goalId = goalId
        val goal = GoalLogic(getApplication()).getGoalForEdit(goalId)
        title.value = goal.title
        initialAmount.value = goal.initialAmount
        targetAmount.value = goal.targetAmount
        currencyCode.value = goal.currencyCode
        iconId.value = goal.icon
        dueDate.value = goal.dueDate
        modifiedDate = goal.modifiedDate
        notificationEnabled .value = goal.notificationEnabled
    }

    fun hasPhotoIcon(): Boolean {
        return GoalLogic(getApplication()).hasPhotoIcon(iconId.value!!)
    }

    fun save() {
        GoalLogic(getApplication()).saveGoal(goalId, title.value!!, initialAmount.value!!, targetAmount.value!!, currencyCode.value!!, iconId.value!!, photoIcon.value, dueDate.value!!, notificationEnabled.value!!)
    }

    fun getIconResourceId(): Int {
        return GoalLogic(getApplication()).getIconResourceId(iconId.value!!)
    }

    fun delete() {
        GoalLogic(getApplication()).deleteGoal(goalId)
    }
}