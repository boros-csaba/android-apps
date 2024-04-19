package com.boroscsaba.myapplication.view

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.boroscsaba.commonlibrary.activities.FragmentBase
import com.boroscsaba.myapplication.R
import com.boroscsaba.myapplication.technical.CompletedGoalRecyclerViewAdapter
import com.boroscsaba.myapplication.viewmodel.OverviewViewModel
import kotlinx.android.synthetic.main.fragment_completed_goals.*


class CompletedGoalsView : FragmentBase(R.layout.fragment_completed_goals) {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModel = (this.activity as OverviewView).viewModel as OverviewViewModel
        viewModel.completedGoals.observe(this, Observer { refreshViews() })
        viewModel.selectedCurrency.observe(this, Observer { refreshViews() })
    }

    override fun setListeners() {

    }

    private fun refreshViews() {
        val viewModel = (this.activity as OverviewView).viewModel as OverviewViewModel
        val currencyCode = viewModel.selectedCurrency.value ?: return
        val goalsWithCurrency = viewModel.completedGoals.value?.filter { it -> it.currencyCode.value == currencyCode }
        if (goalsWithCurrency == null || goalsWithCurrency.isEmpty())
        {
            completedGoalsLabel.text = String.format("%s: %s", currencyCode, resources.getString(R.string.no_completed_goals_yet))
        }
        else {
            completedGoalsLabel.text = String.format("%s %s", resources.getString(R.string.completed_goals), currencyCode)
        }
        initializeRecyclerView(currencyCode)
    }

    private fun initializeRecyclerView(currencyCode: String) {
        val viewModel = (this.activity as OverviewView).viewModel as OverviewViewModel
        completedGoalsRecyclerView.layoutManager = LinearLayoutManager(activity)
        val goals = viewModel.completedGoals.value?.filter { it -> it.currencyCode.value == currencyCode }?.toMutableList()
        if (goals != null) {
            val adapter = CompletedGoalRecyclerViewAdapter(this.activity as OverviewView, goals)
            completedGoalsRecyclerView.adapter = adapter
        }
    }
}


