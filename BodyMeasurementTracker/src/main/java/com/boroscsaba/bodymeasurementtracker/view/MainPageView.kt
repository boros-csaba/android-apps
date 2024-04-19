package com.boroscsaba.bodymeasurementtracker.view

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.View

import com.boroscsaba.bodymeasurementtracker.R
import com.boroscsaba.bodymeasurementtracker.model.Parameter
import com.boroscsaba.bodymeasurementtracker.viewmodel.MainViewModel
import com.boroscsaba.commonlibrary.activities.FragmentBase
import kotlinx.android.synthetic.main.fragment_main.*
import com.boroscsaba.bodymeasurementtracker.MeasurementPresetTypeEnum
import com.boroscsaba.bodymeasurementtracker.activities.MainActivity
import com.boroscsaba.commonlibrary.tutorial.TutorialHelper
import com.boroscsaba.commonlibrary.tutorial.TutorialsEnum
import kotlinx.android.synthetic.main.show_manage_dashboard_tutorial_layout.*
import kotlin.collections.ArrayList


class MainPageView : FragmentBase(R.layout.fragment_main) {

    init {

    }

    private var viewModel: MainViewModel? = null
    private var parametersOrder = ArrayList<Int>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        }
        viewModel!!.parameters.observe(this, Observer { value ->
            if (value != null) {
                for (fragment in childFragmentManager.fragments) {
                    if (fragment.tag == "AddNewParameter") {
                        childFragmentManager.beginTransaction()
                                .remove(fragment).commitNow()
                        childFragmentManager.executePendingTransactions()
                    }
                }

                val newParameterOrder = value.map { parameter -> parameter.id }
                /*var shouldUpdate = newParameterOrder.size != parametersOrder.size
                if (!shouldUpdate) {
                    for (i in 0 until parametersOrder.size) {
                        if (newParameterOrder[i] != parametersOrder[i]) {
                            shouldUpdate = true
                        }
                    }
                }*/

                //if (shouldUpdate) {
                    parametersOrder = ArrayList(newParameterOrder)
                    initializeStatsBlocks(value)
                //}
            }
        })


        val tutorialHelper = TutorialHelper(activity!!.application)
        if (!tutorialHelper.isTutorialCompleted(TutorialsEnum.OPEN_DASHBOARD_MANAGER)) {
            showManageDashboardTutorialStub.inflate()
        }
    }

    override fun setListeners() {
        manageDashboardButton.setOnClickListener {
            showManageDashboardTutorialContainer?.visibility = View.GONE
            val intent = Intent(activity, DashboardSettingsView::class.java)
            startActivity(intent)
        }
        addParameterButton.setOnClickListener {
            (activity as MainActivity).showAddNewParameterView()
        }
    }

    fun setDisplayMode() : Boolean {
        if (!isAdded) return false
        var handled = false
        for (fragment in childFragmentManager.fragments) {
            fragment.view?.visibility = View.VISIBLE
            if (fragment.tag == "BMI") {
                handled = handled || (fragment as DashboardItemBmiView).setDisplayMode()
            }
            else if (parametersOrder.any{ id -> id.toString() == fragment.tag }) {
                handled = handled || (fragment as DashboardItemView).setDisplayMode()
            }
        }
        return handled
    }

    fun setEditMode(tag: String) {
        childFragmentManager.fragments.filter { f -> f.tag != tag }
                .forEach { f -> f.view?.visibility = View.GONE }
    }

    private fun initializeStatsBlocks(value: List<Parameter>) {
        val orderedValues = value
                .filter { p -> p.dashboardEnabled }
                .sortedBy { p -> p.dashboardOrder }

        for (fragment in childFragmentManager.fragments) {
            if (fragment.tag == "BMI" || parametersOrder.any{ id -> id.toString() == fragment.tag }) {
                childFragmentManager.beginTransaction()
                        .remove(fragment)
                        .commitNow()
                childFragmentManager.executePendingTransactions()
            }
        }
        orderedValues.forEach { parameter ->
            if (parameter.presetType == MeasurementPresetTypeEnum.BMI) {
                childFragmentManager.beginTransaction()
                    .add(R.id.statsBlocks, DashboardItemBmiView(), "BMI")
                    .commitNow()
                childFragmentManager.executePendingTransactions()
            }
            else {
                childFragmentManager.beginTransaction()
                        .add(R.id.statsBlocks, DashboardItemView.newInstance(parameter.id), parameter.id.toString())
                        .commitNow()
                childFragmentManager.executePendingTransactions()
            }
        }
    }
}


