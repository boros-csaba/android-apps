package com.boroscsaba.bodymeasurementtracker.activities

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.boroscsaba.bodymeasurementtracker.R
import com.boroscsaba.bodymeasurementtracker.view.InitialSetupView
import com.boroscsaba.bodymeasurementtracker.view.MainPageView
import com.boroscsaba.bodymeasurementtracker.view.MeasurementsEditorPopup
import com.boroscsaba.bodymeasurementtracker.view.NewParameterView
import com.boroscsaba.bodymeasurementtracker.viewmodel.MainViewModel
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.commonlibrary.activities.ActivityDrawerBase
import com.boroscsaba.commonlibrary.activities.AdsDisplayOptions
import com.boroscsaba.commonlibrary.tutorial.TutorialHelper
import com.boroscsaba.commonlibrary.tutorial.TutorialsEnum
import com.google.android.gms.ads.AdSize
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : ActivityDrawerBase(R.id.drawerLayout, R.mipmap.ic_launcher) {

    init {
        options.withViewModel(MainViewModel::class.java)
        options.layout = R.layout.activity_main
        options.toolbarId = R.id.toolbar
        options.adsOptions = AdsDisplayOptions(AdSize.SMART_BANNER, R.id.AdContainer, "ca-app-pub-7535188687645300/3026560772")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = viewModel as MainViewModel
        viewModel.parameters.observe(this, Observer { parameters ->
            val tutorialHelper = TutorialHelper(application)
            if (!parameters.any{ p -> p.presetType.value < 100 } && (!tutorialHelper.isTutorialCompleted(TutorialsEnum.APP_FIRST_START) || !tutorialHelper.isTutorialCompleted(TutorialsEnum.ADD_WEIGHT_HEIGHT_SEX))) {
                supportFragmentManager.beginTransaction()
                        .add(R.id.container, InitialSetupView(), "InitialSetupView")
                        .commit()
            }
            else {
                showMainView()
            }
        })
    }

    fun showMainView() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainPageView(), "MainPageView")
                .commit()
        mainFab.visibility = View.VISIBLE
    }

    fun showAddNewParameterView() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, NewParameterView(), "NewParameterView")
                .commit()
    }

    override fun setListeners() {
        super.setListeners()
        val viewModel = viewModel as MainViewModel
        mainFab.setOnClickListener { MeasurementsEditorPopup().show(viewModel, this) }
        historyButton.setOnClickListener { Utils.openActivity(this, StatsAndHistoryActivity::class.java, 0) }
        statsButton.setOnClickListener { Utils.openActivity(this, StatsAndHistoryActivity::class.java, 1) }
    }

    override fun onBackPressed() {
        val newParameterView = supportFragmentManager.findFragmentByTag("NewParameterView")
        if (newParameterView != null) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.discard_changed))
            builder.setCancelable(true)
            builder.setPositiveButton(getString(R.string.discard)) { dialog, _ ->
                dialog.cancel()
                showMainView()
            }
            builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
            val alert = builder.create()
            alert.show()
            return
        }

        if (container.visibility == View.VISIBLE) {
            val mainPageView = supportFragmentManager.findFragmentByTag("MainPageView")
            if (mainPageView is MainPageView) {
                if (mainPageView.setDisplayMode())
                {
                    return
                }
            }
        }

        super.onBackPressed()
    }

}
