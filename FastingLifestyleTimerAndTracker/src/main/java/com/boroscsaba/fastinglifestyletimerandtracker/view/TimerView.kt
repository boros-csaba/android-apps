package com.boroscsaba.fastinglifestyletimerandtracker.view

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.boroscsaba.commonlibrary.activities.FragmentBase
import com.boroscsaba.fastinglifestyletimerandtracker.R
import com.boroscsaba.fastinglifestyletimerandtracker.viewmodel.TimerViewModel
import kotlinx.android.synthetic.main.fragment_timer.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.format.DateUtils
import android.view.View
import android.widget.NumberPicker
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import com.boroscsaba.commonlibrary.ThemeManager
import com.boroscsaba.commonlibrary.activities.AdsDisplayOptions
import com.boroscsaba.commonlibrary.viewelements.Popup
import com.boroscsaba.fastinglifestyletimerandtracker.technical.NotificationPublisher
import com.google.android.gms.ads.AdSize
import kotlinx.android.synthetic.main.start_target_dialog.view.*
import java.util.*


class TimerView: FragmentBase(R.layout.fragment_timer) {

    init {
        options.adsOptions = AdsDisplayOptions(AdSize.SMART_BANNER, R.id.AdContainer, "ca-app-pub-7535188687645300/7760609792")
    }

    var navigateAction: (() -> Unit)? = null

    private var viewModel: TimerViewModel? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity?.run {
            ViewModelProviders.of(this).get(TimerViewModel::class.java)
        }
        viewModel?.runningFast?.observe(this, Observer { fast ->
            if (fast == null) {
                timer.stopTimer()
                cancelNotification()
                startButton.visibility = View.VISIBLE
                startDescription.visibility = View.VISIBLE
                stopButton.visibility = View.GONE
                timeCounterEdit.visibility = View.GONE
                timeCounterDescription.visibility = View.GONE
            }
            else {
                startButton.visibility = View.GONE
                startDescription.visibility = View.GONE
                stopButton.visibility = View.VISIBLE
                timeCounterEdit.visibility = View.VISIBLE
                timeCounterDescription.visibility = View.VISIBLE
                timer.resumeTimer(fast.startDate, fast.targetHours, fast.targetMinutes)
                scheduleNotification()
            }
        })
    }

    override fun setListeners() {
        timer.onStopListener = { stop() }
        timer.onStartListener = { endDate, targetHours, targetMinutes -> viewModel?.onStartTimer(endDate, targetHours, targetMinutes) }
        timer.updateTimerText = { timerText, isOver ->
            timeCounter.text = timerText
            if (isOver) {
                timeCounter.setTextColor(Color.parseColor(if (ThemeManager.isDarkMode) "#b53d2a" else "#7f0000"))
                timeCounterDescription.text = getString(R.string.times_up)
            }
            else {
                timeCounter.setTextColor(Color.parseColor("#ffffff"))
                timeCounterDescription.text = getString(R.string.timer_description)
            }
        }
        startButton.setOnClickListener {
            if (viewModel?.recordExistsForToday() == true) {
                showOverrideExistingRecordDialog()
            }
            else {
                showStartDialog()
            }
        }
        stopButton.setOnClickListener { stop() }
        timeCounterEdit.setOnClickListener {
            val fast = viewModel?.runningFast?.value
            if (fast != null) {
                val initialStartDate = fast.startDate
                val initialTargetHours = fast.targetHours
                val initialTargetMinutes = fast.targetMinutes

                val popup = Popup(R.layout.running_fast_edit_dialog, activity!!)
                popup.title = getString(R.string.running_timer)
                popup.okAction = { timer.resumeTimer(fast.startDate, fast.targetHours, fast.targetMinutes); popup.popup?.dismiss()  }
                popup.showDiscardButton = true
                popup.closeAction = {
                    fast.startDate = initialStartDate
                    fast.targetHours = initialTargetHours
                    fast.targetMinutes = initialTargetMinutes
                }

                popup.show { view ->
                    val startDateCalendar = Calendar.getInstance()
                    startDateCalendar.timeInMillis = timer.timerStartDate
                    viewModel?.showStartedAtText(fast, view)
                    val goalText = "${String.format("%2d", fast.targetHours)}h ${String.format("%02d", fast.targetMinutes)}m"
                    view.findViewById<TextView>(R.id.goal)?.text = goalText
                    view.findViewById<TextView>(R.id.started_at_time)?.setOnClickListener { viewModel?.editDate(view, activity!!, fast, true) }
                    view.findViewById<ImageView>(R.id.started_at_time_button)?.setOnClickListener { viewModel?.editDate(view, activity!!, fast, true) }
                    view.findViewById<TextView>(R.id.goal)?.setOnClickListener { viewModel?.editGoal(view, activity!!, fast) }
                    view.findViewById<ImageView>(R.id.goalButton)?.setOnClickListener { viewModel?.editGoal(view, activity!!, fast) }
                }
            }
        }
    }

    private fun showOverrideExistingRecordDialog() {
        val dialogBuilder = AlertDialog.Builder(ContextThemeWrapper(context, com.boroscsaba.commonlibrary.R.style.AlertDialog))
        dialogBuilder.setTitle("Replace record ?")
        dialogBuilder.setMessage("You already completed your eating window for today! Do you want to replace the eating window you already logged ?")
        dialogBuilder.setPositiveButton("Replace") { _, _ ->
            showStartDialog()
        }
        dialogBuilder.setNegativeButton(R.string.cancel) { _, _ -> }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#00BCD4"))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#00BCD4"))
    }

    @SuppressLint("InflateParams")
    private fun showStartDialog() {
        val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.start_target_dialog, null, false)
        val dialogBuilder = AlertDialog.Builder(ContextThemeWrapper(context, com.boroscsaba.commonlibrary.R.style.AlertDialog))
        dialogBuilder.setTitle(R.string.start_eating_window)
        dialogBuilder.setView(view)
        dialogBuilder.setCancelable(false)
        dialogBuilder.setPositiveButton(R.string.start_eating_window) { _, _ ->
            timer.startTimer(view.hoursPicker.value, view.minutesPicker.value)
            scheduleNotification()
        }
        dialogBuilder.setNegativeButton(R.string.cancel) { _, _ -> }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#00BCD4"))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#00BCD4"))
        val hoursPicker = alertDialog.findViewById<NumberPicker>(R.id.hoursPicker)
        hoursPicker?.isSaveFromParentEnabled = false
        hoursPicker?.isSaveEnabled = true
        val minutesPicker = alertDialog.findViewById<NumberPicker>(R.id.minutesPicker)
        minutesPicker?.isSaveFromParentEnabled = false
        minutesPicker?.isSaveEnabled = true
        hoursPicker?.minValue = 1
        hoursPicker?.maxValue = 11
        hoursPicker?.value = viewModel?.lastTargetHours?.value ?: 4
        minutesPicker?.minValue = 0
        minutesPicker?.maxValue = 59
        minutesPicker?.value = viewModel?.lastTargetMinutes?.value ?: 0
    }

    private fun stop() {
        val runningFast = viewModel?.runningFast?.value ?: return
        val dialogBuilder = AlertDialog.Builder(ContextThemeWrapper(context, com.boroscsaba.commonlibrary.R.style.AlertDialog))
        dialogBuilder.setTitle("End eating window")
        if (timer.timerTargetEndDate >= System.currentTimeMillis()) {
            dialogBuilder.setMessage("Great! You stopped eating even before your eating window was over!")
            dialogBuilder.setNegativeButton("Keep eating") { _, _ -> }
        }
        else {
            dialogBuilder.setMessage("You have exceeded your eating window! If you just forgot to stop the timer you can edit the end date later.")
        }
        dialogBuilder.setPositiveButton("End eating window") { _, _ ->
            runningFast.startDate = timer.timerStartDate
            runningFast.endDate = System.currentTimeMillis()
            runningFast.id = viewModel?.fasts?.value?.firstOrNull { fast -> DateUtils.isToday(fast.startDate) }?.id ?: 0
            viewModel?.stopTimer()
            cancelNotification()
            viewModel?.showFastEditorPopup(runningFast, context!!, true) { navigateAction?.invoke() }
        }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.parseColor("#00BCD4"))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.parseColor("#00BCD4"))
    }

    private fun scheduleNotification() {
        val app = activity?.application ?: return
        val notificationIntent = Intent(app, NotificationPublisher::class.java)
        val pendingIntent = PendingIntent.getBroadcast(app, 1, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timer.timerTargetEndDate - (15 * 60 * 1000), pendingIntent)
    }

    private fun cancelNotification() {
        val app = activity?.application ?: return
        val notificationIntent = Intent(app, NotificationPublisher::class.java)
        val pendingIntent = PendingIntent.getBroadcast(app, 1, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}