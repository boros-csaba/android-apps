package com.boroscsaba.myapplication.view

import androidx.lifecycle.Observer
import android.os.Bundle
import android.widget.TextView
import com.boroscsaba.commonlibrary.activities.FragmentBase
import com.boroscsaba.commonlibrary.viewelements.charts.HalfPieChartView
import com.boroscsaba.myapplication.R
import com.boroscsaba.myapplication.viewmodel.OverviewViewModel
import kotlinx.android.synthetic.main.activity_transactions_list.*
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit


class OverviewFragment : FragmentBase(R.layout.fragment_overview) {

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
        val currencyCode = viewModel.selectedCurrency.value
        val goalsWithCurrency = viewModel.goalsSource.value!!.filter { it.currencyCode == currencyCode }

        if (goalsWithCurrency.isEmpty()) {
        }
        else {
            var totalTarget = 0.0
            var totalAmount = 0.0
            for (goal in goalsWithCurrency) {
                totalTarget += goal.targetAmount
                totalAmount += goal.initialAmount + goal.transactions.sumByDouble{ t -> t.amount }
            }
            val percentage = totalAmount * 100 / totalTarget
            view?.findViewById<HalfPieChartView>(R.id.halfPieChart)?.setPercentage(percentage.toFloat())

            val currency = goalsWithCurrency.first().currency
            currentSumView.setup(totalAmount, currency)
            targetSumView.setup(totalTarget, currency)

            val completedGoals = viewModel.goalsSource.value?.filter{ g -> g.currencyCode == currencyCode && g.initialAmount + g.transactions.sumByDouble{ t -> t.amount } >= g.targetAmount }
            val activeGoals = viewModel.goalsSource.value?.filter{ g -> g.currencyCode == currencyCode && g.initialAmount + g.transactions.sumByDouble{ t -> t.amount } < g.targetAmount }

            val totalSaved = viewModel.goalsSource.value?.filter { g -> g.currencyCode == currencyCode }?.sumByDouble { g -> g.initialAmount + g.transactions.sumByDouble { t -> t.amount } }
            view?.findViewById<TextView>(R.id.totalSavedText)?.text = currency.getAmountString(totalSaved ?: 0.0)

            val currentTotal = completedGoals?.sumByDouble { g -> g.initialAmount + g.transactions.sumByDouble { t -> t.amount } }
            view?.findViewById<TextView>(R.id.currentTotalText)?.text = currency.getAmountString(currentTotal ?: 0.0)

            view?.findViewById<TextView>(R.id.completedGoalsText)?.text = completedGoals?.count().toString()
            view?.findViewById<TextView>(R.id.activeGoalsText)?.text = activeGoals?.count().toString()

            val transactions = ArrayList<com.boroscsaba.myapplication.model.Transaction>()
            viewModel.goalsSource.value?.forEach{ g -> transactions.addAll(g.transactions) }
            val firstGoalDate = transactions.minBy { it.transactionDate }?.transactionDate ?: 0
            val totalLeft = totalTarget - totalAmount
            if (firstGoalDate == 0.toLong()) {
                view?.findViewById<TextView>(R.id.transactionsText)?.text = "0"
                view?.findViewById<TextView>(R.id.dailyAverageText)?.text = currency.getAmountString(0.0)
                view?.findViewById<TextView>(R.id.lastMonthSavingsText)?.text = currency.getAmountString(0.0)
                view?.findViewById<TextView>(R.id.lastWeekSavingsText)?.text = currency.getAmountString(0.0)
                view?.findViewById<TextView>(R.id.averageTransactionText)?.text = "-"
                view?.findViewById<TextView>(R.id.expectedCompletionText)?.text = "-"
            }
            else {
                val daysPassed = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - firstGoalDate)

                val dailyAverage = transactions.sumByDouble { it.amount } / daysPassed
                view?.findViewById<TextView>(R.id.dailyAverageText)?.text = currency.getAmountString(dailyAverage)

                val last7DaysSavings = transactions.filter { it.transactionDate >= System.currentTimeMillis() - (7f * 24 * 60 * 60 * 1000) }.sumByDouble { it.amount }
                view?.findViewById<TextView>(R.id.lastWeekSavingsText)?.text = currency.getAmountString(last7DaysSavings)
                val last30DaysSavings = transactions.filter { it.transactionDate >= System.currentTimeMillis() - (30f * 24  * 60 * 60 * 1000) }.sumByDouble { it.amount }
                view?.findViewById<TextView>(R.id.lastMonthSavingsText)?.text = currency.getAmountString(last30DaysSavings)

                view?.findViewById<TextView>(R.id.averageTransactionText)?.text = currency.getAmountString(transactions.sumByDouble { it.amount } / transactions.count())
                val format = DecimalFormat("###,###.##")
                view?.findViewById<TextView>(R.id.expectedCompletionText)?.text = String.format("%s %s", format.format((totalLeft / dailyAverage)), resources.getString(R.string.days))
            }
            view?.findViewById<TextView>(R.id.transactionsText)?.text = transactions.count().toString()
            view?.findViewById<TextView>(R.id.amountLeftText)?.text = currency.getAmountString(totalLeft)
        }
    }
}