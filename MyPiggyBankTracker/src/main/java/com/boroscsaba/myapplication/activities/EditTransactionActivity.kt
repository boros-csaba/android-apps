package com.boroscsaba.myapplication.activities

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.boroscsaba.commonlibrary.activities.ActivityBase
import com.boroscsaba.commonlibrary.helpers.KeyboardHelper
import com.boroscsaba.commonlibrary.settings.SettingsHelper

import com.boroscsaba.myapplication.dataAccess.GoalRepository
import com.boroscsaba.myapplication.dataAccess.TransactionRepository
import com.boroscsaba.myapplication.R
import com.boroscsaba.myapplication.model.Transaction
import com.boroscsaba.myapplication.view.GoalsListView
import kotlinx.android.synthetic.main.activity_edit_transaction.*

import java.text.DecimalFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale



class EditTransactionActivity : ActivityBase(), View.OnClickListener {

    private var titleTextView: TextView? = null
    private var datePickerDialog: DatePickerDialog? = null

    private var transactionId: Int = 0
    private var goalId: Int = 0
    private var transaction: Transaction? = null
    private var selectedDate: Calendar = Calendar.getInstance(Locale.US)
    private var isTitleGreen = true
    private var transactionModified = false
    private var isNegativeTransaction = false

    init {
        options.layout = R.layout.activity_edit_transaction
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.down_in, R.anim.stay)

        transactionId = 0
        goalId = 0

        val extras = intent.extras
        transactionId = extras?.getInt("TRANSACTION_ID", 0) ?: 0
        goalId = extras?.getInt("GOAL_ID", 0) ?: 0

        setupActionBar()
        selectedDate = Calendar.getInstance(Locale.US)

        if (transactionId != 0) {
            val transactionRepository = TransactionRepository(application)
            transaction = transactionRepository.getObjectById(transactionId)
        }
        else {
            transaction = Transaction(this)
            transaction?.transactionDate = System.currentTimeMillis()
            transaction?.goalId = goalId
        }

        initViews()

        isNegativeTransaction = extras?.getBoolean(GoalsListView.TRANSACTION_IS_NEGATIVE_KEY, false) ?: false
        if (isNegativeTransaction) {
            setNegativeSignInitialValue()
            setNegativeAccentColor()
        }
    }

    override fun setListeners() {
    }

    private fun setupActionBar() {
        val myToolbar = findViewById<Toolbar>(R.id.edit_transaction_toolbar)
        setSupportActionBar(myToolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            val goalRepository = GoalRepository(application)
            val goal = goalRepository.getObjectById(goalId)
            if (goal != null) {
                actionBar.title = goal.title
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_transaction_menu, menu)
        if (transaction == null || transaction!!.id == 0) {
            (0 until menu.size())
                    .map { menu.getItem(it) }
                    .filter { it.title === getString(R.string.delete) }
                    .forEach { it.isVisible = false }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.edit_transaction_action_save -> {
                trySave()
                true
            }
            R.id.edit_goal_action_delete -> {
                askToDelete()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (!mapChangesToTransaction()) {
            super.onBackPressed()
            overridePendingTransition(R.anim.stay, R.anim.down_out)
            return
        }
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.discard_changed))
        builder.setCancelable(true)
        builder.setPositiveButton(getString(R.string.discard)) { _, _ ->
            super.onBackPressed()
            overridePendingTransition(R.anim.stay, R.anim.down_out)
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    private fun trySave() {
        var hasErrors = false
        val targetAmount = try {
                java.lang.Double.parseDouble(transaction_amount.editText?.text?.toString()!!)
            } catch (ex: Exception) {
                0.0
            }
        if (targetAmount == 0.0) {
            transaction_amount.error = getString(R.string.cannot_be_zero)
            hasErrors = true
        } else {
            transaction_amount.error = null
        }

        if (!hasErrors) {
            save()
        } else {
            val keyboardHelper = KeyboardHelper(this)
            keyboardHelper.hideKeyboard()
        }
    }

    private fun mapChangesToTransaction() : Boolean {
        val transaction = this.transaction ?: return false
        var changed = false

        val title = transaction_title.editText?.text?.toString()
        if (title != null && transaction.title != title) {
            transaction.title = title
            changed = true
        }

        val amountText = transaction_amount.editText?.text?.toString()
        var amount = 0.0
        try {
            amount = java.lang.Double.parseDouble(amountText!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (transaction.amount != amount) {
            transaction.amount = amount
            changed = true
        }

        if (transaction.transactionDate != selectedDate.timeInMillis) {
            transaction.transactionDate = selectedDate.timeInMillis
            changed = true
        }

        if (changed) {
            transactionModified = true
        }
        return changed || transactionModified
    }

    override fun save(): Boolean {
        if (mapChangesToTransaction()) {
            val transactionRepository = TransactionRepository(application)
            transactionRepository.upsert(transaction!!, false)
        }
        finish()
        overridePendingTransition(R.anim.stay, R.anim.down_out)
        return true
    }

    private fun initViews() {
        titleTextView = findViewById(R.id.edit_transaction_title_textView)
        transaction_date.editText?.requestFocus()
        transaction_date.editText?.setOnClickListener(this)

        transaction_amount.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                handleAmountChange()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        transaction_date.editText?.inputType = InputType.TYPE_NULL
        val newCalendar = Calendar.getInstance()
        datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val newDate = Calendar.getInstance()
            newDate.set(year, monthOfYear, dayOfMonth)
            selectedDate = newDate
            transaction_date.editText?.setText(SettingsHelper(application).getDateFormat().format(newDate.time))
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH))

        if (transaction!!.title != "")
            transaction_title.editText?.setText(transaction!!.title)

        if (transaction!!.id > 0) {
            val format = DecimalFormat("#.##")
            transaction_amount.editText?.setText(format.format(transaction!!.amount))
        }

        transaction_date.editText?.setText(SettingsHelper(application).getDateFormat().format(transaction!!.transactionDate))
        val transactionDate = Date(transaction!!.transactionDate)
        val calendar = GregorianCalendar()
        calendar.time = transactionDate
        selectedDate = calendar

        datePickerDialog!!.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    }

    private fun askToDelete() {
        val transactionRepository = TransactionRepository(application)
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.activity_edit_transaction_delete_alert))
        builder.setCancelable(true)
        builder.setPositiveButton(
                getString(R.string.yes)
        ) { dialog, _ ->
            dialog.cancel()
            transactionRepository.delete(transactionId, false)
            finish()
        }
        builder.setNegativeButton(
                getString(R.string.no)
        ) { dialog, _ -> dialog.cancel() }

        val alert = builder.create()
        alert.show()
    }

    override fun onClick(view: View) {
        datePickerDialog!!.show()
    }

    private fun setPositiveAccentColor() {
        if (!isTitleGreen) {
            isTitleGreen = true
            titleTextView!!.setTextColor(ResourcesCompat.getColor(resources, R.color.green, null))
        }
    }

    private fun setNegativeAccentColor() {
        if (isTitleGreen) {
            isTitleGreen = false
            titleTextView!!.setTextColor(ResourcesCompat.getColor(resources, R.color.red, null))
        }
    }

    private fun handleAmountChange() {
        val amountText = transaction_amount.editText?.text.toString()
        var amount = 0.0
        try {
            amount = java.lang.Double.parseDouble(amountText)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (amountText == "" && isNegativeTransaction) {
            setNegativeSignInitialValue()
            setNegativeAccentColor()
        }
        else if (amount < 0) setNegativeAccentColor()
        else setPositiveAccentColor()
    }

    private fun setNegativeSignInitialValue() {
        transaction_amount.editText?.setText("-")
    }
}
