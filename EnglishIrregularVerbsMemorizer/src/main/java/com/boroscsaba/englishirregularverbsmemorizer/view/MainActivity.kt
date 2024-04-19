package com.boroscsaba.englishirregularverbsmemorizer.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.boroscsaba.commonlibrary.activities.ActivityDrawerBase
import com.boroscsaba.englishirregularverbsmemorizer.R
import com.boroscsaba.englishirregularverbsmemorizer.viewmodel.VerbViewModel
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.Rect
import android.speech.tts.TextToSpeech
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.commonlibrary.activities.helpers.ToolbarButton
import com.boroscsaba.commonlibrary.tutorial.TutorialHelper
import com.boroscsaba.commonlibrary.tutorial.TutorialsEnum
import com.boroscsaba.englishirregularverbsmemorizer.activities.StatsActivity
import com.boroscsaba.englishirregularverbsmemorizer.logic.VerbLogic
import java.util.*


class MainActivity: ActivityDrawerBase(R.id.drawerLayout, R.mipmap.ic_launcher), TextView.OnEditorActionListener, TextToSpeech.OnInitListener {

    init {
        options.layout = R.layout.activity_main
        options.toolbarId = R.id.toolbar
    }

    private var isSaving = false
    private var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        textToSpeech = TextToSpeech(this, this)

        val statsButton = ToolbarButton(R.string.stats, R.drawable.ic_insert_chart_white_48dp)
        statsButton.action = {
            val intent = Intent(this,StatsActivity::class.java)
            startActivity(intent)
            false
        }
        buttons.add(statsButton)
        dailyProgress.setDailyGoal(VerbLogic(application).getNumberOfReps())

        viewModel = ViewModelProviders.of(this).get(VerbViewModel::class.java)
        val viewModel = viewModel as VerbViewModel
        viewModel.currentVerb.observe(this, Observer { verb ->
            isSaving = false
            if (verb == null) {
                verbTextView.setText(R.string.no_more_verbs)
            }
            else {
                verbTextView.text = verb.infinitive
            }
            simplePast.setText("")
            pastParticiple.setText("")
        })
        viewModel.progressText.observe(this, Observer { progressText ->
            setPageTitle(progressText)
        })
        viewModel.todayProgress.observe(this, Observer { progress ->
            dailyProgress.setProgress(progress)
        })
        viewModel.goalMet.observe(this, Observer { goalMet ->
            if (goalMet == true) {
                viewModel.showDailyGoalMetPopup(this)
            }
        })
        viewModel.correctAnswer.observe(this, Observer {
            val slideAnimator = ValueAnimator.ofInt(window.decorView.height / 2, 0).setDuration(1200)
            slideAnimator.addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                val layoutParams = (okImage.layoutParams as ConstraintLayout.LayoutParams)
                layoutParams.bottomMargin = value
                okImage.layoutParams = layoutParams
            }
            slideAnimator.addListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    val layoutParams = (okImage.layoutParams as ConstraintLayout.LayoutParams)
                    layoutParams.bottomMargin = 2 * window.decorView.width
                    okImage.layoutParams = layoutParams
                    verbTextView.visibility = View.VISIBLE
                }
            })
            val set = AnimatorSet()
            set.play(slideAnimator)
            set.interpolator = BounceInterpolator()
            verbTextView.visibility = View.GONE
            set.start()
        })
        viewModel.initialize(intent)

        val isFirstStart = !TutorialHelper(application).isTutorialCompleted(TutorialsEnum.FIRST_VERB)
        if (!isFirstStart) {
            simplePastTutorial.visibility = View.GONE
            simplePastArrow.visibility = View.GONE
            pastParticipleTutorial.visibility = View.GONE
            pastParticipleArrow.visibility = View.GONE
        }
        else {
            TutorialHelper(application).completeTutorial(TutorialsEnum.FIRST_VERB)
        }

        window.decorView.viewTreeObserver.addOnPreDrawListener {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val layoutParams = (okImage.layoutParams as ConstraintLayout.LayoutParams)
            layoutParams.bottomMargin = displayMetrics.heightPixels
            okImage.layoutParams = layoutParams
            val screenHeight = displayMetrics.heightPixels
            val r = Rect()
            contentWrapper.getWindowVisibleDisplayFrame(r)
            val heightDifference = screenHeight - (r.bottom - r.top)
            val newHeight = screenHeight - heightDifference - toolbar.height
            if (contentWrapper.layoutParams.height != newHeight) {
                contentWrapper.layoutParams.height = newHeight
                contentWrapper.requestLayout()
            }
            true
        }
    }

    override fun onInit(status: Int) {
        if(status != TextToSpeech.ERROR) {
            textToSpeech?.language = Locale.ENGLISH
            textToSpeech?.setSpeechRate(0.5f)
        }
        else {
            LoggingHelper.logException(Exception("TextToSpeech error $status"), this)
        }
    }

    override fun onPause() {
        super.onPause()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }

    private fun setPageTitle(title: String) {
        toolbarTitle.text = title
    }

    override fun setListeners() {
        super.setListeners()
        val viewModel = viewModel as VerbViewModel
        pastParticiple.setOnEditorActionListener(this)
        sendAnswer.setOnClickListener { evaluateAnswer() }
        listenButton.setOnClickListener {
            val text = viewModel.currentVerb.value?.infinitive
            if (text != null) {
                textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, text)
            }
        }
    }

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == KeyEvent.KEYCODE_ENTER) {
            if (!isSaving) {
                evaluateAnswer()
            }
        }
        return true
    }

    private fun evaluateAnswer() {
        val viewModel = viewModel as VerbViewModel
        isSaving = true
        simplePastTutorial.visibility = View.GONE
        simplePastArrow.visibility = View.GONE
        pastParticipleTutorial.visibility = View.GONE
        pastParticipleArrow.visibility = View.GONE
        viewModel.saveAnswer(simplePast.text.toString(), pastParticiple.text.toString(), this)
    }
}