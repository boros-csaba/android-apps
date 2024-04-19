package com.boroscsaba.myapplication.technical

import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.boroscsaba.commonlibrary.adapters.DiffUtilCallback
import com.boroscsaba.commonlibrary.adapters.IRecyclerViewListener
import com.boroscsaba.commonlibrary.settings.SettingsHelper
import com.boroscsaba.commonlibrary.viewelements.currency.CurrencyManager

import com.boroscsaba.myapplication.R
import com.boroscsaba.myapplication.logic.PercentageDisplayType
import com.boroscsaba.myapplication.dataAccess.GoalMapper
import com.boroscsaba.myapplication.viewmodel.GoalListItemViewModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.goal_row_layout.view.*
import kotlinx.android.synthetic.main.goal_row_layout_edit.view.*
import kotlinx.android.synthetic.main.progress_bar.view.*
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Boros Csaba
 */

class GoalRecyclerViewAdapter(private val context: AppCompatActivity, internal val recyclerViewListener: IRecyclerViewListener?, var values: ArrayList<GoalListItemViewModel>, private val isEditable: Boolean) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    var isEditMode: Boolean = false

    fun changeValues(values: ArrayList<GoalListItemViewModel>) {
        val result = DiffUtil.calculateDiff(DiffUtilCallback(this.values, values))
        this.values = values
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isEditMode && isEditable) R.layout.goal_row_layout_edit else R.layout.goal_row_layout
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        val viewModel = values[position]
        val viewHolder = holder as ViewHolder
        viewHolder.id = viewModel.id

        val handle = holder.itemView.reorderHandle
        if (handle != null) {
            handle.setOnTouchListener { _, event ->
                if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_DOWN) {
                    recyclerViewListener?.onStartDrag(holder)
                }
                false
            }
        }
        else {
            holder.itemView.setOnClickListener { recyclerViewListener?.onClick(holder) }
            viewModel.percentage.observe(context, Observer { value ->
                setPercentageText(viewModel, holder)
                val layerDrawable = holder.itemView.progressBarContainer.progressBarImage.drawable as LayerDrawable
                val clipDrawable = layerDrawable.findDrawableByLayerId(R.id.progressBar_primaryProgressBar) as ClipDrawable
                clipDrawable.level = (value!!.toDouble() * 100).toInt()
            })
            viewModel.targetAmount.observe(context, Observer { setPercentageText(viewModel, holder) })
            viewModel.currentAmount.observe(context, Observer { setPercentageText(viewModel, holder) })

            holder.itemView.progressBarContainer.progressBarImage.setPadding(0, -10, 0, 0)
            viewModel.dueDate.observe(context, Observer { value ->
                if (value ?: 0 > 0) {
                    holder.itemView.findViewById<TextView>(R.id.dueDateLabel)?.visibility = View.VISIBLE
                    val dueDateValueEditText = holder.itemView.findViewById<TextView>(R.id.dueDateValue)
                    dueDateValueEditText?.visibility = View.VISIBLE
                    dueDateValueEditText?.text = SettingsHelper(context.application).getDateFormat().format(value)
                    val dueLeftTextView = holder.itemView.findViewById<TextView>(R.id.dueLeftValue)
                    dueLeftTextView?.visibility = View.VISIBLE

                    val date = value ?: 0
                    when {
                        DateUtils.isToday(date) -> {
                            dueLeftTextView.setText(R.string.due_today)
                            dueLeftTextView.setTextColor(Color.parseColor("#f47100"))
                            dueLeftTextView.setTypeface(null, Typeface.BOLD)
                        }
                        DateUtils.isToday(date - DateUtils.DAY_IN_MILLIS) -> {
                            dueLeftTextView.setText(R.string.due_tomorrow)
                            dueLeftTextView.setTextColor(Color.parseColor("#ff8d00"))
                            dueLeftTextView.setTypeface(null, Typeface.BOLD)
                        }
                        date < System.currentTimeMillis() -> {
                            dueLeftTextView.setText(R.string.overdue)
                            dueLeftTextView.setTextColor(Color.parseColor("#e54304"))
                            dueLeftTextView.setTypeface(null, Typeface.BOLD)
                        }
                        else -> {
                            val nrOfDays = TimeUnit.MILLISECONDS.toDays(date - System.currentTimeMillis()) + 1
                            dueLeftTextView.text = "$nrOfDays days left"
                            dueLeftTextView.setTextColor(Color.parseColor("#888888"))
                            dueLeftTextView.setTypeface(null, Typeface.NORMAL)
                        }
                    }
                }
                else {
                    holder.itemView.findViewById<TextView>(R.id.dueDateLabel)?.visibility = View.GONE
                    holder.itemView.findViewById<TextView>(R.id.dueDateValue)?.visibility = View.GONE
                    holder.itemView.findViewById<TextView>(R.id.dueLeftValue)?.visibility = View.GONE
                }
            })
        }

        val iconView = holder.itemView.findViewById(R.id.icon) as ImageView
        viewModel.icon.observe(context, Observer { changeIcon(iconView, viewModel) })
        viewModel.modifiedDate.observe(context, Observer { changeIcon(iconView, viewModel) })

        viewModel.title.observe(context, Observer{ value ->
            holder.itemView.findViewById<TextView>(R.id.title).text = value
        })
    }

    private fun changeIcon(iconView: ImageView, viewModel: GoalListItemViewModel) {
        if (viewModel.hasPhotoIcon()) {
            val modifiedDate = viewModel.modifiedDate.value
            if (modifiedDate != null) {
                Glide.with(context).load(GoalMapper(context).getImageUri(viewModel.id, modifiedDate)).into(iconView)
            }
        }
        else {
            Glide.with(context).load(viewModel.getIconResourceId()).into(iconView)
        }
    }

    internal fun onItemMove(fromPosition: Int, toPosition: Int) {
        val goal1 = values[fromPosition]
        val goal2 = values[toPosition]
        values[fromPosition] = goal2
        values[toPosition] = goal1

        notifyItemMoved(fromPosition, toPosition)
    }

    private fun setPercentageText(viewModel: GoalListItemViewModel, holder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        val decimalFormat = DecimalFormat("0.##")
        val setting = PercentageDisplayType.values().firstOrNull { v -> v.value == SettingsHelper(context.application).getValue("PERCENTAGE_DISPLAY_SETTINGS") } ?: PercentageDisplayType.Percentage
        val currency = CurrencyManager.getCurrency(viewModel.currencyCode.value ?: "USD")
        val text = when (setting) {
            PercentageDisplayType.Percentage -> "${decimalFormat.format(viewModel.percentage.value)}%"
            PercentageDisplayType.Collected -> " in"
            PercentageDisplayType.Left -> " left"
        }

        if (setting == PercentageDisplayType.Percentage) {
            holder.itemView.percentageAmount.visibility = View.GONE
        }
        else {
            if (setting == PercentageDisplayType.Collected) {
                holder.itemView.percentageAmount.setup((viewModel.currentAmount.value ?: 0).toDouble(), currency)
            }
            else {
                holder.itemView.percentageAmount.setup((viewModel.targetAmount.value ?: 0).toDouble() - (viewModel.currentAmount.value ?: 0).toDouble(), currency)
            }
        }
        holder.itemView.percentageText.text = text
    }

    override fun getItemCount(): Int {
        return values.size
    }
}
