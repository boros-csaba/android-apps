package com.boroscsaba.myapplication.view

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import android.text.format.DateUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.commonlibrary.activities.ActivityBase
import com.boroscsaba.commonlibrary.adapters.SimpleRecyclerViewAdapter
import com.boroscsaba.commonlibrary.helpers.AppRatingHelper
import com.boroscsaba.commonlibrary.settings.SettingsHelper

import com.boroscsaba.myapplication.ProgressBar
import com.boroscsaba.myapplication.R
import com.boroscsaba.myapplication.activities.EditTransactionActivity
import com.boroscsaba.myapplication.activities.EditGoalActivity
import com.boroscsaba.myapplication.dataAccess.GoalMapper
import com.boroscsaba.myapplication.model.Transaction
import com.boroscsaba.myapplication.viewmodel.GoalDetailsViewModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_transactions_list.*
import kotlinx.android.synthetic.main.goal_statisticts_layout.view.*
import kotlinx.android.synthetic.main.transaction_row_layout.view.*

import java.text.DecimalFormat
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

/**
 * Created by Boros Csaba
 */

class GoalDetailsView : ActivityBase() {

    private var progressBar: ProgressBar? = null

    init {
        options.layout = R.layout.activity_transactions_list
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(GoalDetailsViewModel::class.java)
        val viewModel = viewModel as GoalDetailsViewModel
        viewModel.goalSource.observe(this, Observer { value -> if (value == null || value.id == 0) finish() })
        viewModel.title.observe(this, Observer { value ->
            toolbar.title = value
            goalTitle.text = value
        })
        viewModel.currentSum.observe(this, Observer { onAmountChange() })
        viewModel.targetAmount.observe(this, Observer { onAmountChange() })
        viewModel.currency.observe(this, Observer { onAmountChange() })
        viewModel.icon.observe(this, Observer { changeIcon() })
        viewModel.modifiedDate.observe(this, Observer { changeIcon() })
        viewModel.percentage.observe(this, Observer { value ->
            if (value != null) {
                val decimalFormat = DecimalFormat("0.##")
                progressBar?.setText(decimalFormat.format(value) + "%")
                if (value <= 100) progressBar?.setPercentage(floor(value).toInt())
                else {
                    val currentSum: Double = viewModel.currentSum.value ?: 0.0
                    val targetAmount: Double = viewModel.targetAmount.value ?: 0.0
                    val progress = max(10.0, floor((currentSum - targetAmount) * 100 / currentSum)).toInt()
                    progressBar?.setSecondaryPercentage(progress)
                }
            }
        })
        viewModel.transactions.observe(this, Observer { value ->
            if (value != null) {
                if (transaction_listView.adapter == null) {
                    transaction_listView.layoutManager = LinearLayoutManager(this)
                    transaction_listView.adapter = SimpleRecyclerViewAdapter(value, R.layout.transaction_row_layout, { holder, item ->
                        holder.itemView.transactionAmountView.setup(item.amount, item.goal!!.currency)
                        holder.itemView.transactionAmountView.setTextColor(if (item.amount < 0) Color.parseColor("#f02f31") else Color.parseColor("#83b400"))
                        holder.itemView.transaction_row_title_textView.text = item.title
                        holder.itemView.transaction_row_date_textView.text = Utils.getFormattedDateString(item.transactionDate, application)
                        holder.itemView.setOnClickListener { editTransaction(item.id) }
                    }, R.layout.goal_statisticts_layout, { holder, _ ->
                        val goal = viewModel.goalSource.value
                        if (goal != null) {
                            holder.itemView.goalAddedOn.text = getString(R.string.goal_added_on, Utils.getFormattedDateString(goal.createdDate, this))
                            val latestTransaction = goal.transactions.lastOrNull()
                            val passedDays = if (viewModel.completed.value == true && latestTransaction != null) Utils.getNrOfDaysPassed(goal.createdDate, latestTransaction.transactionDate) else Utils.getNrOfDaysPassed(goal.createdDate)
                            holder.itemView.goalOpenFor.text = getString(R.string.goal_was_open_for, passedDays)
                            val dailyAverage = if (passedDays == 0) {
                                goal.transactions.sumByDouble { t -> t.amount }
                            }
                            else {
                                goal.transactions.sumByDouble { t -> t.amount } / passedDays
                            }
                            holder.itemView.savedOnAverage.setup(dailyAverage, goal.currency)
                            if (viewModel.completed.value == true || dailyAverage < 0.001) {
                                holder.itemView.expectedEndDate.text = ""
                                holder.itemView.expectedEndDateLabel.visibility = View.GONE
                            }
                            else {
                                val totalAmount = goal.targetAmount - (goal.initialAmount + goal.transactions.sumByDouble { t -> t.amount })
                                val completedInDays = ceil(totalAmount / dailyAverage).toInt()
                                val completionDate = Utils.getFormattedDateString(System.currentTimeMillis() + (completedInDays.toLong() * 24 * 60 * 60 * 1000), this)
                                holder.itemView.expectedEndDate.text = getString(R.string.completed_in_days_text, completedInDays, completionDate)
                                holder.itemView.expectedEndDateLabel.visibility = View.VISIBLE
                            }
                        }
                    })
                }
                else {
                    @Suppress("UNCHECKED_CAST")
                    val adapter = transaction_listView.adapter as SimpleRecyclerViewAdapter<Transaction>
                    adapter.changeValues(value)
                    adapter.notifyDataSetChanged()
                }
            }
        })
        viewModel.completed.observe(this, Observer { value ->
            if (value == true && !intent.getBooleanExtra("alreadyCompleted", false)) {
                performGoalCompletedAnimations()
                AppRatingHelper.showRateAppSnackBar(this)
            }
        })
        viewModel.dueDate.observe(this, Observer { value ->
            if (value ?: 0 > 0) {
                dueDateLabel.visibility = View.VISIBLE
                dueDateValue.visibility = View.VISIBLE
                dueDateValue.text = SettingsHelper(application).getDateFormat().format(value)
                dueLeftValue.visibility = View.VISIBLE
                val date = value ?: 0
                when {
                    DateUtils.isToday(date) -> {
                        dueLeftValue.setText(R.string.due_today)
                        dueLeftValue.setTextColor(Color.parseColor("#f47100"))
                        dueLeftValue.setTypeface(null, Typeface.BOLD)
                    }
                    DateUtils.isToday(date - DateUtils.DAY_IN_MILLIS) -> {
                        dueLeftValue.setText(R.string.due_tomorrow)
                        dueLeftValue.setTextColor(Color.parseColor("#ff8d00"))
                        dueLeftValue.setTypeface(null, Typeface.BOLD)
                    }
                    date < System.currentTimeMillis() -> {
                        dueLeftValue.setText(R.string.overdue)
                        dueLeftValue.setTextColor(Color.parseColor("#e54304"))
                        dueLeftValue.setTypeface(null, Typeface.BOLD)
                    }
                    else -> {
                        val nrOfDays = TimeUnit.MILLISECONDS.toDays(date - System.currentTimeMillis()) + 1
                        dueLeftValue.text = String.format(getString(R.string.days_left), nrOfDays)
                        dueLeftValue.setTextColor(Color.parseColor("#888888"))
                        dueLeftValue.setTypeface(null, Typeface.NORMAL)
                    }
                }
            }
            else {
                dueDateLabel.visibility = View.GONE
                dueDateValue.visibility = View.GONE
                dueLeftValue.visibility = View.GONE
            }
        })
        viewModel.initialize(intent)

        initViews()
        setSupportActionBar(toolbar)
        setupActionBar()
    }

    override fun setListeners() {
        floatingAddButton.setOnClickListener { addNewTransaction(false) }
        floatingSubtractButton.setOnClickListener { addNewTransaction(true) }
        congratulationsBackground.setOnClickListener { congratulationsBackground.visibility = View.GONE }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val viewModel = viewModel as GoalDetailsViewModel
        menuInflater.inflate(R.menu.transactions_list_menu, menu)
        menu.findItem(R.id.menuEditGoal).setOnMenuItemClickListener {
            val intent = Intent(this, EditGoalActivity::class.java)
            intent.putExtra("GOAL_ID", viewModel.goalId)
            val iconTransition = Pair(icon as View, "goalIconTransition")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, iconTransition)
            startActivity(intent, options.toBundle())
            true
        }
        return true
    }

    private fun onAmountChange() {
        val viewModel = viewModel as GoalDetailsViewModel
        val currency = viewModel.currency.value ?: return
        val currentSum = viewModel.currentSum.value ?: return
        val targetSum = viewModel.targetAmount.value ?: return

        currentSumView.setup(currentSum, currency)
        targetSumView.setup(targetSum, currency)
    }

    private fun changeIcon() {
        val viewModel = viewModel as GoalDetailsViewModel
        if (viewModel.hasPhotoIcon()) {
            val modifiedDate = viewModel.modifiedDate.value
            if (modifiedDate != null) {
                Glide.with(this).load(GoalMapper(this).getImageUri(viewModel.goalId, modifiedDate)).into(icon)
            }
        }
        else {
            Glide.with(this).load(viewModel.getIconResourceId()).into(icon)
        }
    }

    private fun initViews() {
        progressBar = ProgressBar(this)
    }

    private fun addNewTransaction(negativeTransaction: Boolean) {
        val viewModel = viewModel as GoalDetailsViewModel
        val intent = Intent(this, EditTransactionActivity::class.java)
        intent.putExtra("TRANSACTION_ID", 0)
        intent.putExtra("GOAL_ID", viewModel.goalId)
        intent.putExtra(TRANSACTION_IS_NEGATIVE_KEY, negativeTransaction)
        startActivityForResult(intent, EDIT_TRANSACTION_REQUEST_CODE)
    }

    private fun editTransaction(transactionId: Int) {
        val viewModel = viewModel as GoalDetailsViewModel
        val intent = Intent(this, EditTransactionActivity::class.java)
        intent.putExtra("TRANSACTION_ID", transactionId)
        intent.putExtra("GOAL_ID", viewModel.goalId)
        startActivityForResult(intent, EDIT_TRANSACTION_REQUEST_CODE)
    }

    private fun performGoalCompletedAnimations() {
        congratulationsBackground.visibility = View.VISIBLE
        val bgAnimation = congratulationsBackground.animate().alpha(1f)
        bgAnimation.startDelay = 1000
        bgAnimation.duration = 500
        bgAnimation.interpolator = AccelerateInterpolator()
        bgAnimation.start()

        congratulationsPopup.scaleX = 0f
        congratulationsPopup.scaleY = 0f
        val popupAnimation = congratulationsPopup.animate().scaleX(1f).scaleY(1f)
        popupAnimation.startDelay = 1200
        popupAnimation.duration = 300
        popupAnimation.interpolator = AccelerateInterpolator()
        popupAnimation.start()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val EDIT_TRANSACTION_REQUEST_CODE = 2
        private const val TRANSACTION_IS_NEGATIVE_KEY = "transactionIsNegative"
    }
}
