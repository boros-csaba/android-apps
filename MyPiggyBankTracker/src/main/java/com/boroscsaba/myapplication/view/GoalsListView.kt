package com.boroscsaba.myapplication.view

import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.GravityCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.BounceInterpolator
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import com.boroscsaba.commonlibrary.activities.ActivityDrawerBase
import com.boroscsaba.commonlibrary.LoggingHelper

import com.boroscsaba.commonlibrary.activities.AdsDisplayOptions
import com.boroscsaba.myapplication.technical.GoalRecyclerViewAdapter
import com.boroscsaba.myapplication.technical.ItemTouchHelperCallback
import com.boroscsaba.commonlibrary.adapters.IRecyclerViewListener
import com.boroscsaba.myapplication.technical.ViewHolder
import com.boroscsaba.myapplication.R
import com.boroscsaba.myapplication.activities.EditGoalActivity
import com.boroscsaba.myapplication.viewmodel.GoalListItemViewModel
import com.boroscsaba.myapplication.viewmodel.GoalsListViewModel
import com.google.android.gms.ads.AdSize

import java.util.ArrayList

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.goal_row_layout.view.*

class GoalsListView : ActivityDrawerBase(R.id.drawerLayout, R.mipmap.ic_launcher), IRecyclerViewListener {

    private var touchHelper: ItemTouchHelper? = null

    init {
        options.layout = R.layout.activity_main
        options.toolbarId = R.id.toolbar
        options.adsOptions = AdsDisplayOptions(AdSize.SMART_BANNER, R.id.AdContainer, "ca-app-pub-7535188687645300/7496236596")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(GoalsListViewModel::class.java)
        val viewModel = viewModel as GoalsListViewModel
        viewModel.goals.observe(this, Observer<ArrayList<GoalListItemViewModel>> { value ->
            if (value != null) {
                initializeRecyclerView(value)
            }
        })
        viewModel.initialize(intent)
    }

    private fun initializeRecyclerView(value: ArrayList<GoalListItemViewModel>) {
        if (recyclerView.adapter == null) {
            recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
            val adapter = GoalRecyclerViewAdapter(this, this, value, true)
            recyclerView.adapter = adapter
            val callback = ItemTouchHelperCallback(recyclerView, adapter)
            touchHelper = ItemTouchHelper(callback)
            touchHelper!!.attachToRecyclerView(recyclerView)
        }
        else {
            val goalRecyclerViewAdapter = recyclerView.adapter as GoalRecyclerViewAdapter
            goalRecyclerViewAdapter.changeValues(value)
            goalRecyclerViewAdapter.notifyDataSetChanged()
        }
        if (value.isEmpty()) {
            showTutorial()
        }
        else {
            hideTutorial()
        }
    }

    override fun setListeners() {
        addButton.setOnClickListener {
            val intent = Intent(this, EditGoalActivity::class.java)
            intent.putExtra("GOAL_ID", 0)
            startActivity(intent)
        }

        cancelReorderButton.setOnClickListener {
            val viewModel = viewModel as GoalsListViewModel
            val adapter = recyclerView.adapter as GoalRecyclerViewAdapter
            adapter.isEditMode = false
            viewModel.undoReordering()
            onEditModeChange(false)
        }

        confirmReorderButton.setOnClickListener {
            val viewModel = viewModel as GoalsListViewModel
            val adapter = recyclerView.adapter as GoalRecyclerViewAdapter
            adapter.isEditMode = false
            viewModel.saveGoalOrder()
            onEditModeChange(false)
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onClick(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        val holder = viewHolder as ViewHolder
        val intent = Intent(this, GoalDetailsView::class.java)
        intent.putExtra("GOAL_ID", holder.id)
        val iconTransition = Pair(holder.itemView.icon as View, "goalIconTransition")
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, iconTransition)
        startActivityForResult(intent, GOAL_REQUEST_CODE, options.toBundle())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val overviewMenuItem = menu.add(R.string.non_translated_drawer_close)
        overviewMenuItem.setIcon(R.drawable.ic_insert_chart_white_48dp)
        overviewMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        overviewMenuItem.setOnMenuItemClickListener {
            val intent = Intent(this, OverviewView::class.java)
            startActivity(intent)
            false
        }
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else {
            super.onBackPressed()
        }
    }

    private fun showTutorial() {
        LoggingHelper.logEvent(this, "MainActivity_Tutorial")
        main_activity_tutorial.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        val animation = AnimationSet(true)
        val scaleAnimation = ScaleAnimation(0.3f, 1f, 0.3f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        animation.addAnimation(scaleAnimation)
        animation.interpolator = BounceInterpolator()
        animation.duration = 1000
        val tutorialImage = findViewById<ImageView>(R.id.main_activity_tutorial_image)
        tutorialImage.startAnimation(animation)
    }

    private fun hideTutorial() {
        main_activity_tutorial.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    @SuppressLint("RestrictedApi")
    fun onEditModeChange(isEditMode: Boolean) {
        if (isEditMode) {
            addButton.visibility = View.GONE
            confirmReorderButton.visibility = View.VISIBLE
            cancelReorderButton.visibility = View.VISIBLE
        } else {
            addButton.visibility = View.VISIBLE
            confirmReorderButton.visibility = View.GONE
            cancelReorderButton.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        if (recyclerView?.adapter != null) {
            val goalRecyclerViewAdapter = recyclerView.adapter as GoalRecyclerViewAdapter
            goalRecyclerViewAdapter.notifyDataSetChanged()
        }
    }

    override fun onStartDrag(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        touchHelper?.startDrag(viewHolder)
    }

    companion object {
        const val GOAL_REQUEST_CODE = 2
        const val TRANSACTION_IS_NEGATIVE_KEY = "transactionIsNegative"
    }
}