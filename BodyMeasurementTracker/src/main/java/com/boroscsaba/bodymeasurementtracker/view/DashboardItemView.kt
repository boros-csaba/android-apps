package com.boroscsaba.bodymeasurementtracker.view

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.boroscsaba.bodymeasurementtracker.R
import com.boroscsaba.bodymeasurementtracker.model.MeasurementLogEntry
import com.boroscsaba.bodymeasurementtracker.viewmodel.ParameterViewModel
import com.boroscsaba.commonlibrary.activities.helpers.TabsPagerFragmentAdapter
import com.boroscsaba.commonlibrary.adapters.SimpleRecyclerViewAdapter
import com.boroscsaba.commonlibrary.helpers.KeyboardHelper
import com.boroscsaba.commonlibrary.settings.SettingsHelper
import kotlinx.android.synthetic.main.dashboard_item_current_value_view.*
import kotlinx.android.synthetic.main.dashboard_item_view.*
import kotlinx.android.synthetic.main.edit_history_item_popup.view.*
import kotlinx.android.synthetic.main.history_edit_row_layout.view.*
import java.util.*
import kotlin.random.Random


class DashboardItemView : Fragment(), androidx.viewpager.widget.ViewPager.OnPageChangeListener {

    private var parameterId: Int = 0
    private var viewModel: ParameterViewModel? = null
    private val viewChangeTime = Random.nextInt(20000, 30000).toLong()
    private var viewChangedByHand = false
    private var viewChangedAutomatically = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dashboard_item_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViewPager()
        parameterId = arguments?.getInt("parameterId") ?: return
        viewModel = ViewModelProviders.of(this).get(ParameterViewModel::class.java)

        val handler = Handler()
        val viewModel = this.viewModel ?: return
        viewModel.parameter.observe(this, Observer { parameter ->
            if (parameter != null) {
                if (parameter.name != statsLabel.text) {
                    statsLabel.text = parameter.name
                    statsLabelEdit.setText(parameter.name)
                }
                initHistoryItems(parameter.measurements)
                parameterIcon.setup(parameter)
            }
            else {
                statsBlockWrapper.visibility = View.GONE
            }
        })
        viewModel.changeViewCounter.observe(this, Observer {
            val canChangeView = statsLabel.visibility == View.VISIBLE && !viewChangedByHand
            if (canChangeView) {
                val nextIndex = if (contentViewPager.currentItem < 1) contentViewPager.currentItem + 1 else 0
                viewChangedAutomatically = true
                contentViewPager.setCurrentItem(nextIndex, true)
                handler.postDelayed({ viewModel.changeView() }, viewChangeTime)
            }
        })
        viewModel.initialize(parameterId)

        quickEditButton.setOnClickListener {
            contentViewPager.setCurrentItem(0, false)
            setEditMode()
        }
        deleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(context!!)
            builder.setMessage("Are you sure you want to permanently delete this parameter with all the measurements ? ")
            builder.setCancelable(true)
            builder.setPositiveButton(getString(com.boroscsaba.commonlibrary.R.string.delete)) { dialog, _ ->
                dialog.cancel()
                val parentActivity = activity
                if (parentActivity != null) {
                    KeyboardHelper(parentActivity).hideKeyboard()
                }
                viewModel.deleteParameter()
            }
            builder.setNegativeButton(getString(com.boroscsaba.commonlibrary.R.string.cancel)) { dialog, _ -> dialog.cancel() }
            val alert = builder.create()
            alert.show()
        }
        discardChangesButton.setOnClickListener { (parentFragment as MainPageView).setDisplayMode() }
        quickEditSaveButton.setOnClickListener {
            if (saveChanges()) {
                (parentFragment as MainPageView).setDisplayMode()
            }
        }

        handler.postDelayed({ viewModel.changeView() }, viewChangeTime)
    }

    private fun setupViewPager() {
        val currentValueView = DashboardItemCurrentValueView()
        val statsView = DashboardItemStatsView()
        val adapter = TabsPagerFragmentAdapter(childFragmentManager, 2) { position ->
            when (position) {
                0 -> currentValueView
                1 -> statsView
                else -> currentValueView
            }
        }
        contentViewPager.offscreenPageLimit = 3
        contentViewPager.adapter = adapter
        contentViewPager.addOnPageChangeListener(this)
    }

    private fun getDashboardItemCurrentValueView(): DashboardItemCurrentValueView {
        return (contentViewPager.adapter as TabsPagerFragmentAdapter).getItem(0) as DashboardItemCurrentValueView
    }

    private fun setEditMode() {
        (parentFragment as MainPageView).setEditMode(tag!!)
        setFieldsVisibility(true)
        getDashboardItemCurrentValueView().setEditMode()
        separator.visibility = View.VISIBLE
        animateChange(editBar, 0f, 48f)
    }

    fun setDisplayMode(): Boolean {
        val wasEditMode = statsLabel.visibility == View.GONE
        if (!wasEditMode) return false

        setFieldsVisibility(false)
        getDashboardItemCurrentValueView().setDisplayMode()
        separator.visibility = View.GONE
        animateChange(editBar, 48f, 0f)
        return wasEditMode
    }

    private fun setFieldsVisibility(editMode: Boolean) {
        val editorVisibility = if (editMode) View.VISIBLE else View.GONE
        val displayVisibility = if (!editMode) View.VISIBLE else View.GONE

        statsLabel.visibility = displayVisibility
        quickEditButton.visibility = displayVisibility

        statsLabelEdit.visibility = editorVisibility
        deleteButton.visibility = editorVisibility
        discardChangesButton.visibility = editorVisibility
        quickEditSaveButton.visibility = editorVisibility
        historyEditTitleContainer.visibility = editorVisibility
        historyEditList.visibility = editorVisibility
        addNewHistory.visibility = editorVisibility
        editHistoryContainer.visibility = editorVisibility
    }

    private fun saveChanges(): Boolean {
        val newParameterName = statsLabelEdit.text.toString()
        if (newParameterName.length < 2) {
            statsLabelEdit.error = getString(R.string.too_short)
            return false
        }
        val newUnit = lastValueUnit.text.toString()
        viewModel?.saveChanges(newParameterName, newUnit)
        getDashboardItemCurrentValueView().saveChanges()
        return true
    }

    private fun animateChange(view: View, dpOriginalHeight: Float, dpNewHeight: Float) {
        val originalHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpOriginalHeight, resources.displayMetrics).toInt()
        val targetHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpNewHeight, resources.displayMetrics).toInt()
        val slideAnimator = ValueAnimator.ofInt(originalHeight, targetHeight).setDuration(1000)
        slideAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            view.layoutParams.height = value
            view.requestLayout()
        }
        val set = AnimatorSet()
        set.play(slideAnimator)
        set.interpolator = OvershootInterpolator()
        set.start()
    }

    private fun initHistoryItems(values: ArrayList<MeasurementLogEntry>) {
        val app = activity?.application ?: return
        val viewModel = this.viewModel ?: return
        addNewHistory.setOnClickListener { showHistoryEditorPopup(null) }
        values.sortByDescending { v -> v.logDate }
        historyEditList.layoutManager = LinearLayoutManager(activity)
        historyEditList.adapter = SimpleRecyclerViewAdapter(values, R.layout.history_edit_row_layout, { viewHolder, measurement ->
            viewHolder.itemView.logDate.text = SettingsHelper(app).getDateFormat().format(measurement.logDate)
            viewHolder.itemView.logValue.text = String.format("%.1f %s", measurement.value, viewModel.parameter.value?.unit)
            viewHolder.itemView.quickEditHistoryButton.setOnClickListener {
                showHistoryEditorPopup(measurement)
            }
            viewHolder.itemView.deleteHistoryButton.setOnClickListener {
                val builder = AlertDialog.Builder(context!!)
                builder.setMessage("Are you sure you want to delete this measurement ?")
                builder.setCancelable(true)
                builder.setPositiveButton(getString(com.boroscsaba.commonlibrary.R.string.delete)) { dialog, _ ->
                    viewModel.deleteMeasurement(measurement.id)
                    dialog.cancel()
                }
                builder.setNegativeButton(getString(com.boroscsaba.commonlibrary.R.string.cancel)) { dialog, _ -> dialog.cancel() }
                val alert = builder.create()
                alert.show()
            }
        })
    }

    private fun showHistoryEditorPopup(measurement: MeasurementLogEntry?) {
        val app = activity?.application ?: return
        val activity = activity ?: return
        val viewModel = this.viewModel ?: return

        @SuppressLint("InflateParams")
        val popupView = LayoutInflater.from(context).inflate(R.layout.edit_history_item_popup, null) as LinearLayout
        popupView.dateEditText.setText(SettingsHelper(app).getDateFormat().format(measurement?.logDate ?: System.currentTimeMillis()))
        popupView.valueEditText.setText(String.format("%.1f", measurement?.value ?: 0.0))
        popupView.unitText.text = viewModel.parameter.value?.unit
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = measurement?.logDate ?: System.currentTimeMillis()
        popupView.dateEditText.setOnClickListener {
            val datePickerDialog = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)
                popupView.dateEditText.setText(SettingsHelper(app).getDateFormat().format(calendar.timeInMillis))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.show()
        }
        val alertDialog = AlertDialog.Builder(ContextThemeWrapper(context, com.boroscsaba.commonlibrary.R.style.AlertDialog))
                .setTitle("Edit measurement")
                .setPositiveButton(context?.getString(com.boroscsaba.commonlibrary.R.string.save)) { _, _ ->
                    viewModel.saveHistoryEdit(measurement?.id ?: 0, popupView.valueEditText.text.toString(), calendar.timeInMillis)
                    KeyboardHelper(activity).hideKeyboard()
                }
                .setNegativeButton(context?.getString(com.boroscsaba.commonlibrary.R.string.cancel)) { _, _ ->
                    KeyboardHelper(activity).hideKeyboard()
                }
                .setView(popupView)
                .setCancelable(false)
                .show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.parseColor("#00BCD4"))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.parseColor("#00BCD4"))
    }

    override fun onPageSelected(position: Int) {
        if (position == 0) dot1.setColorFilter(dotSelectedColor) else dot1.setColorFilter(dotDefaultColor)
        if (position == 1) dot2.setColorFilter(dotSelectedColor) else dot2.setColorFilter(dotDefaultColor)

        if (viewChangedAutomatically) {
            viewChangedAutomatically = false
        }
        else {
            viewChangedByHand = true
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    companion object {

        private val dotSelectedColor = Color.parseColor("#aaaaaa")
        private val dotDefaultColor = Color.parseColor("#cccccc")

        @JvmStatic
        fun newInstance(parameterId: Int) =
                DashboardItemView().apply {
                    arguments = Bundle().apply {
                        putInt("parameterId", parameterId)
                    }
                }
    }
}
