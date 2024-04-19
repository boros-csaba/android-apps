package com.boroscsaba.bodymeasurementtracker.view

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.boroscsaba.bodymeasurementtracker.R
import com.boroscsaba.bodymeasurementtracker.activities.MainActivity
import com.boroscsaba.bodymeasurementtracker.viewmodel.MainViewModel
import com.boroscsaba.commonlibrary.activities.FragmentBase
import com.boroscsaba.commonlibrary.helpers.KeyboardHelper
import com.boroscsaba.commonlibrary.tutorial.TutorialHelper
import com.boroscsaba.commonlibrary.tutorial.TutorialMessagePopup
import com.boroscsaba.commonlibrary.tutorial.TutorialsEnum
import kotlinx.android.synthetic.main.add_weight_height_layout.*
import kotlinx.android.synthetic.main.fragment_initial_setup.*

class InitialSetupView : FragmentBase(R.layout.fragment_initial_setup) {

    private var viewModel: MainViewModel? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        }

        val tutorialHelper = TutorialHelper(activity!!.application)
        if (!tutorialHelper.isTutorialCompleted(TutorialsEnum.APP_FIRST_START)) {
            tutorialHelper.completeTutorial(TutorialsEnum.APP_FIRST_START)
            val popup = TutorialMessagePopup(R.string.welcome, R.string.first_start_greeting)
            popup.action = { showWeightHeightEditor(tutorialHelper) }
            popup.show(activity!!)
        }
        else {
            showWeightHeightEditor(tutorialHelper)
        }
    }

    private var weightUnit = "lbs"
    private var heightUnit = "in"
    private var isMale = true
    private fun showWeightHeightEditor(tutorialHelper: TutorialHelper) {
        addWeightHeightStub.inflate()
        initialInformation.visibility = View.VISIBLE

        lbs.setOnClickListener {
            weightUnit = "lbs"
            setHighlighted(lbs)
            setNotSelected(kg)
            setNotSelected(st)
        }
        kg.setOnClickListener {
            weightUnit = "kg"
            setHighlighted(kg)
            setNotSelected(lbs)
            setNotSelected(st)
        }
        st.setOnClickListener {
            weightUnit = "st"
            setHighlighted(st)
            setNotSelected(kg)
            setNotSelected(lbs)
        }
        inch.setOnClickListener {
            heightUnit = "in"
            setHighlighted(inch)
            setNotSelected(cm)
        }
        cm.setOnClickListener {
            heightUnit = "cm"
            setHighlighted(cm)
            setNotSelected(inch)
        }
        male.setOnClickListener {
            isMale = true
            male.setColorFilter(Color.parseColor("#555555"))
            female.setColorFilter(Color.parseColor("#aaaaaa"))
        }
        female.setOnClickListener {
            isMale = false
            female.setColorFilter(Color.parseColor("#555555"))
            male.setColorFilter(Color.parseColor("#aaaaaa"))
        }
        saveButton.setOnClickListener {
            var hasError = false
            val weight = weightTextInputLayout.editText?.text?.toString()?.toDoubleOrNull() ?: 0.0
            if (weight <= 0.0) {
                hasError = true
                weightTextInputLayout.error = getString(R.string.must_be_larger_than_0)
            }
            val height = heightTextInputLayout.editText?.text?.toString()?.toDoubleOrNull() ?: 0.0
            if (height <= 0.0) {
                hasError = true
                heightTextInputLayout.error = getString(R.string.must_be_larger_than_0)
            }
            if (!hasError) {
                KeyboardHelper(activity!!).hideKeyboard()
                viewModel?.saveInitialInformation(weight, weightUnit, height, heightUnit, isMale)
                tutorialHelper.completeTutorial(TutorialsEnum.ADD_WEIGHT_HEIGHT_SEX)
                (activity as MainActivity).showMainView()
            }
        }
    }

    private fun setHighlighted(textView: TextView) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20f)
        textView.setTypeface(null, Typeface.BOLD)
    }

    private fun setNotSelected(textView: TextView) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18f)
        textView.setTypeface(null, Typeface.NORMAL)
    }
}