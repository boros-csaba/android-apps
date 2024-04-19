package com.boroscsaba.bodymeasurementtracker.view

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import android.util.TypedValue
import com.boroscsaba.bodymeasurementtracker.MeasurementPresetTypeEnum
import com.boroscsaba.bodymeasurementtracker.R
import com.boroscsaba.bodymeasurementtracker.model.DashboardSettingsItem
import com.boroscsaba.bodymeasurementtracker.viewmodel.DashboardSettingsViewModel
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.commonlibrary.adapters.IRecyclerViewListener
import com.boroscsaba.commonlibrary.activities.ActivityBase
import com.boroscsaba.commonlibrary.adapters.ItemTouchHelperCallback
import com.boroscsaba.commonlibrary.adapters.MovableRecyclerViewAdapter
import com.boroscsaba.commonlibrary.tutorial.TutorialHelper
import com.boroscsaba.commonlibrary.tutorial.TutorialsEnum
import kotlinx.android.synthetic.main.activity_dashboard_settings_view.*
import kotlinx.android.synthetic.main.dashboard_settings_item_layout.view.*
import java.util.ArrayList

class DashboardSettingsView : ActivityBase(), IRecyclerViewListener {

    private var touchHelper: ItemTouchHelper? = null
    private var initialized = false

    init {
        options.layout = R.layout.activity_dashboard_settings_view
        options.toolbarId = R.id.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tutorialHelper = TutorialHelper(application)
        if (!tutorialHelper.isTutorialCompleted(TutorialsEnum.OPEN_DASHBOARD_MANAGER)) {
            tutorialHelper.completeTutorial(TutorialsEnum.OPEN_DASHBOARD_MANAGER)
        }

        viewModel = ViewModelProviders.of(this).get(DashboardSettingsViewModel::class.java)
        val viewModel = viewModel as DashboardSettingsViewModel
        viewModel.dashboardSettingsItems.observe(this, Observer { value ->
            if (value != null && !initialized) {
                value.sortBy{ v -> v.order }
                initializeRecyclerView(value)
                initialized = true
            }
        })
        viewModel.initialize(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val viewModel = viewModel as DashboardSettingsViewModel
        @Suppress("UNCHECKED_CAST")
        viewModel.saveChanges((recyclerView.adapter as MovableRecyclerViewAdapter<DashboardSettingsItem>).values)
    }

    override fun setListeners() { }

    private fun initializeRecyclerView(value: ArrayList<DashboardSettingsItem>) {
        if (recyclerView.adapter == null) {
            recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
            val adapter = MovableRecyclerViewAdapter(this, value, R.layout.dashboard_settings_item_layout, R.id.dragHandle) { viewHolder, position ->
                viewHolder.itemView.letter.text = value[position].letterText
                viewHolder.itemView.title.text = value[position].title
                viewHolder.itemView.settingValue.isChecked = value[position].enabled
                if (value[position].presetType == MeasurementPresetTypeEnum.CUSTOM || value[position].presetType.value > 100) {
                    viewHolder.itemView.icon.setImageDrawable(null)
                }
                else {
                    Utils.setImageViewSource(MeasurementPresetTypeEnum.getIcon(value[position].presetType), viewHolder.itemView.icon, this)
                    val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MeasurementPresetTypeEnum.getIconPadding(value[position].presetType).toFloat(), resources.displayMetrics).toInt()
                    viewHolder.itemView.icon.setPadding(padding, padding, padding, padding)
                }
                viewHolder.itemView.letter.backgroundTintList = ColorStateList.valueOf(Color.parseColor(value[position].color))
                viewHolder.itemView.settingValue.setOnCheckedChangeListener { _, isChecked -> value[position].enabled = isChecked }
            }
            recyclerView.adapter = adapter
            val callback = ItemTouchHelperCallback(adapter)
            touchHelper = ItemTouchHelper(callback)
            touchHelper!!.attachToRecyclerView(recyclerView)
        }
        else {
            @Suppress("UNCHECKED_CAST")
            (recyclerView.adapter as MovableRecyclerViewAdapter<DashboardSettingsItem>).changeValues(value)
            //recyclerView.adapter.notifyDataSetChanged()
        }
    }

    override fun onStartDrag(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        touchHelper?.startDrag(viewHolder)
    }

    override fun onClick(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {}
}
