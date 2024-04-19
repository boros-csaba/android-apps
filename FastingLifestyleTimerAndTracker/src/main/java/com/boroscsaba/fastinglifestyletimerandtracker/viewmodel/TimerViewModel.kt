package com.boroscsaba.fastinglifestyletimerandtracker.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.text.format.DateUtils
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.boroscsaba.commonlibrary.activities.helpers.ViewModel
import com.boroscsaba.commonlibrary.settings.SettingsHelper
import com.boroscsaba.commonlibrary.viewelements.Popup
import com.boroscsaba.commonlibrary.viewelements.SingleValueEditPopup
import com.boroscsaba.fastinglifestyletimerandtracker.R
import com.boroscsaba.fastinglifestyletimerandtracker.logic.FastLogic
import com.boroscsaba.fastinglifestyletimerandtracker.model.Fast
import java.text.SimpleDateFormat
import java.util.*

class TimerViewModel(application: Application) : ViewModel(application) {

    val runningFast = MutableLiveData<Fast>()
    val lastTargetHours = MutableLiveData<Int>()
    val lastTargetMinutes = MutableLiveData<Int>()
    val fasts = MutableLiveData<ArrayList<Fast>>()

    private val dateFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

    override fun initialize(intent: Intent?) {
        val sharedPreferences = getSharedPreferences()
        FastLogic(getApplication()).getFastHistory(fasts)

        if (sharedPreferences.getBoolean("isTimerRunning", false)) {
            val newFast = Fast(getApplication())
            newFast.startDate = sharedPreferences.getLong("startDate", 0)
            newFast.targetHours = sharedPreferences.getInt("targetHours", 4)
            newFast.targetMinutes = sharedPreferences.getInt("targetMinutes", 0)
            runningFast.value = newFast
        }
        else {
            runningFast.value = null
        }

        lastTargetHours.value = sharedPreferences.getInt("targetHours", 4)
        lastTargetMinutes.value = sharedPreferences.getInt("targetMinutes", 0)
    }

    fun stopTimer() {
        val editor = getSharedPreferences().edit()
        editor.putBoolean("isTimerRunning", false)
        runningFast.value = null
        editor.apply()
    }

    fun onStartTimer(startDate: Long, targetHours: Int, targetMinutes: Int) {
        val editor = getSharedPreferences().edit()
        editor.putBoolean("isTimerRunning", true)
        editor.putLong("startDate", startDate)
        editor.putInt("targetHours", targetHours)
        editor.putInt("targetMinutes", targetMinutes)
        editor.apply()

        val newFast = runningFast.value ?: Fast(getApplication())
        newFast.startDate = startDate
        newFast.targetHours = targetHours
        newFast.targetMinutes = targetMinutes
        runningFast.value = newFast
    }

    private fun saveFast(fastId: Int, startData: Long, endDate: Long, targetHours: Int, targetMinutes: Int) {
        FastLogic(getApplication()).saveFast(fastId, startData, endDate, targetHours, targetMinutes)
    }

    private fun deleteFast(fastId: Int) {
        FastLogic(getApplication()).deleteFast(fastId)
    }

    fun recordExistsForToday(): Boolean {
        return fasts.value?.any { fast ->
            DateUtils.isToday(fast.startDate)
        } ?: false

    }

    fun showFastEditorPopup(fast: Fast, context: Context, promptOnDiscard: Boolean, okAction: (() -> Unit)? = null) {
        val initialStartDate = fast.startDate
        val initialEndDate = fast.endDate
        val initialTargetHours = fast.targetHours
        val initialTargetMinutes = fast.targetMinutes

        val dateString = SettingsHelper(getApplication()).getDateFormat().format(fast.startDate)
        val popup = Popup(R.layout.fast_edit_dialog, context)
        popup.okAction = {
            saveFast(fast.id, fast.startDate, fast.endDate, fast.targetHours, fast.targetMinutes)
            okAction?.invoke()
            popup.popup?.dismiss()
        }

        if (fast.id > 0) {
            popup.title = dateString
            popup.deleteAction = { deleteFast(fast.id) }
            popup.deleteAlertResourceId = R.string.delete_sure
        }
        else {
            popup.title = context.getString(R.string.new_eating_window)
            if (promptOnDiscard) {
                popup.closeAlertResourceId = R.string.discard_changed
            }
        }

        popup.closeAction = {
            fast.startDate = initialStartDate
            fast.endDate = initialEndDate
            fast.targetHours = initialTargetHours
            fast.targetMinutes = initialTargetMinutes
        }

        popup.show { view ->
            showStartedAtText(fast, view)
            showEndedAtText(fast, view)
            calculateDuration(fast, view)
            val goalText = "${String.format("%2d", fast.targetHours)}h ${String.format("%02d", fast.targetMinutes)}m"
            if (fast.id <= 0) {
                view.findViewById<TextView>(R.id.date)?.text = dateString
            }
            else {
                view.findViewById<LinearLayout>(R.id.dateContainer)?.visibility = View.GONE
            }
            view.findViewById<TextView>(R.id.goal)?.text = goalText
            view.findViewById<TextView>(R.id.started_at_time)?.
                    setOnClickListener { editDate(view, context, fast, true) }
            view.findViewById<ImageView>(R.id.started_at_time_button)?.
                    setOnClickListener { editDate(view, context, fast, true) }

            view.findViewById<TextView>(R.id.ended_at_time)?.
                    setOnClickListener { editDate(view, context, fast, false) }
            view.findViewById<ImageView>(R.id.ended_at_time_button)?.
                    setOnClickListener { editDate(view, context, fast, false) }

            view.findViewById<TextView>(R.id.goal)?.
                    setOnClickListener { editGoal(view, context, fast) }
            view.findViewById<ImageView>(R.id.goalButton)?.
                    setOnClickListener { editGoal(view, context, fast) }
        }
    }

    fun editDate(alertDialog: View, context: Context, fast: Fast, isStartDate: Boolean) {
        val editedDate = if (isStartDate) fast.startDate else fast.endDate
        SingleValueEditPopup(context)
                .withValue(editedDate, SingleValueEditPopup.Type.DateTime)
                .onSaveDateTime { value ->
                    if (isStartDate) fast.startDate = value else fast.endDate = value
                    if (fast.startDate > fast.endDate) fast.endDate += 24 * 60 * 60 * 1000
                    while (fast.startDate + 24 * 60 * 60 * 1000 < fast.endDate) {
                        fast.endDate -= 24 * 60 * 60 * 1000
                    }
                    showStartedAtText(fast, alertDialog)
                    showEndedAtText(fast, alertDialog)
                    calculateDuration(fast, alertDialog)
                }
                .show()
    }

    fun editGoal(alertDialog: View, context: Context, fast: Fast) {
        SingleValueEditPopup(context)
                .withValue(fast.targetHours, fast.targetMinutes)
                .onSaveHoursMinutes { hours, minutes ->
                    fast.targetHours = hours
                    fast.targetMinutes = minutes
                    val goalText = "${String.format("%2d", fast.targetHours)}h ${String.format("%02d", fast.targetMinutes)}m"
                    alertDialog.findViewById<TextView>(R.id.goal)?.text = goalText
                }
                .show()
    }

    fun showStartedAtText(fast: Fast, alertDialog: View) {
        alertDialog.findViewById<TextView>(R.id.started_at_time)?.text = dateFormat.format(fast.startDate)
    }

    private fun showEndedAtText(fast: Fast, alertDialog: View) {
        alertDialog.findViewById<TextView>(R.id.ended_at_time)?.text = dateFormat.format(fast.endDate)
    }

    private fun calculateDuration(fast: Fast, alertDialog: View) {
        var milliseconds = fast.endDate - fast.startDate
        val hours = milliseconds / (1000 * 60 * 60)
        milliseconds -= hours * 1000 * 60 * 60
        val minutes = milliseconds / (1000 * 60)
        milliseconds -= minutes * 1000 * 60
        val seconds = milliseconds / 1000
        val durationText = "${String.format("%02d",hours)}:${String.format("%02d",minutes)}:${String.format("%02d",seconds)}"
        alertDialog.findViewById<TextView>(R.id.duration)?.text = durationText
    }

    private fun getSharedPreferences(): SharedPreferences {
        return getApplication<Application>().getSharedPreferences("Preferences", MODE_PRIVATE)
    }
}