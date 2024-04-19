package com.boroscsaba.englishirregularverbsmemorizer.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import com.boroscsaba.commonlibrary.viewelements.Popup
import com.boroscsaba.englishirregularverbsmemorizer.R
import com.boroscsaba.englishirregularverbsmemorizer.logic.VerbLogic
import com.boroscsaba.englishirregularverbsmemorizer.model.Verb
import kotlinx.android.synthetic.main.wrong_answer_popup.view.*
import android.graphics.Paint
import android.view.View
import android.widget.TextView
import com.boroscsaba.commonlibrary.activities.helpers.ViewModel
import com.boroscsaba.commonlibrary.tutorial.TutorialHelper
import com.boroscsaba.commonlibrary.tutorial.TutorialsEnum


class VerbViewModel(application: Application) : ViewModel(application) {

    val currentVerb = MutableLiveData<Verb?>()
    val progressText = MutableLiveData<String>()
    val todayProgress = MutableLiveData<Int>()
    val goalMet = MutableLiveData<Boolean>()
    val correctAnswer = MutableLiveData<Int>()
    private val dayStreak = MutableLiveData<Int>()
    private val bestDayStreak = MutableLiveData<Int>()

    override fun initialize(intent: Intent?) {
        val isFirstStart = !TutorialHelper(getApplication()).isTutorialCompleted(TutorialsEnum.FIRST_VERB)
        val verbLogic = VerbLogic(getApplication())
        verbLogic.getNextVerb(currentVerb, isFirstStart)
        verbLogic.getProgressText(progressText)
        verbLogic.getTodayProgress(todayProgress)
        verbLogic.getDayStreak(dayStreak, bestDayStreak)
    }

    fun saveAnswer(simplePast: String, pastParticiple: String, context: Context) {
        val verbLogic = VerbLogic(getApplication())
        val verbId = currentVerb.value?.id ?: return
        val verb = currentVerb.value ?: return
        val isSimplePastCorrect = verbLogic.isSimplePastCorrect(simplePast, verb)
        val isPastParticipleCorrect = verbLogic.isPastParticipleCorrect(pastParticiple, verb)
        if (!isSimplePastCorrect || !isPastParticipleCorrect) {
            val popup = Popup(R.layout.wrong_answer_popup, context)
            popup.setColor(Color.parseColor("#ef5350"))
            popup.title = context.getString(R.string.wrong_answer)
            popup.okAction = { popup.popup?.dismiss() }
            popup.show { view ->
                view.verbTextView.text = verb.infinitive
                if (isSimplePastCorrect) {
                    view.simplePastLabel.visibility = View.GONE
                    view.simplePast.visibility = View.GONE
                    view.pastParticiple.visibility = View.GONE
                }
                else {
                    view.simplePast.text = simplePast
                    view.simplePast.paintFlags = view.simplePast.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    view.pastParticiple.text = verb.simplePast.replace("/", " or ")
                }
                if (isPastParticipleCorrect) {
                    view.pastParticipleLabel.visibility = View.GONE
                    view.wrongPastParticiple.visibility = View.GONE
                    view.correctPastParticiple.visibility = View.GONE
                }
                else {
                    view.wrongPastParticiple.text = pastParticiple
                    view.wrongPastParticiple.paintFlags = view.wrongPastParticiple.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    view.correctPastParticiple.text = verb.pastParticiple.replace("/", " or ")
                }
            }
        }
        else {
            correctAnswer.value = correctAnswer.value ?: 0 + 1
        }
        verbLogic.saveAnswerAndGetNextVerb(verbId, isSimplePastCorrect && isPastParticipleCorrect, currentVerb, goalMet)
    }

    fun showDailyGoalMetPopup(context: Context) {
        val popup = Popup(R.layout.daily_goal_met_popup, context)
        popup.title = context.getString(R.string.congratulations)
        popup.setColor(Color.parseColor("#50d393"))
        popup.okAction = { popup.popup?.dismiss() }
        popup.show { view ->
            val currentText = "${dayStreak.value} day streak!"
            view.findViewById<TextView>(R.id.currentStreakTextView).text = currentText
            val bestText = "(Best: ${bestDayStreak.value} days in a row)"
            view.findViewById<TextView>(R.id.bestStreakTextView).text = bestText
        }
    }
}