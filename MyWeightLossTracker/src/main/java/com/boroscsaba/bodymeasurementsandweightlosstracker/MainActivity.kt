package com.boroscsaba.myweightlosstracker

import android.view.View
import com.boroscsaba.myweightlosstracker.model.Measurement
import com.boroscsaba.commonlibrary.activities.ActivityDrawerBase
import com.boroscsaba.myweightlosstracker.model.Parameter
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.commonlibrary.viewelements.popupEditor.PopupEditor
import kotlinx.android.synthetic.main.measurement_row_layout.view.*
import kotlinx.android.synthetic.main.parameter_row_layout.view.*
import kotlin.math.abs

class MainActivity : ActivityDrawerBase(R.id.drawerLayout, R.mipmap.ic_launcher) {

    init {
        options.withViewModel(ViewModel::class.java)
                    .withNavigationDrawer()
                        .withViewPager()
                            .withBottomTabsLayout()
                            .addHistoryView(Parameter::class.java, { (viewModel as ViewModel).parameters }, R.layout.parameter_row_layout, { holder, item ->
                                    holder.itemView.parameterNameTextView.text = item.name
                                    val lastMeasurement = item.measurements.maxBy { m -> m.logDate }
                                    if (lastMeasurement == null) {
                                        holder.itemView.changeDirection.visibility = View.INVISIBLE
                                        holder.itemView.changeAmount.visibility = View.GONE
                                        holder.itemView.parameterValue.visibility = View.GONE
                                        holder.itemView.dateTextView.visibility = View.GONE
                                        holder.itemView.addFirstMeasurementLabel.visibility = View.VISIBLE
                                        holder.itemView.tutorialArrow.visibility = View.VISIBLE
                                    }
                                    else {
                                        holder.itemView.parameterValue.visibility = View.VISIBLE
                                        holder.itemView.dateTextView.visibility = View.VISIBLE
                                        holder.itemView.addFirstMeasurementLabel.visibility = View.GONE
                                        holder.itemView.tutorialArrow.visibility = View.GONE
                                        /*val valueText = String.format("%.1f ", lastMeasurement.value) + item.unit
                                        holder.itemView.parameterValue.text = valueText
                                        holder.itemView.dateTextView.text = getString(R.string.on_date, Utils.getFormattedDateString(lastMeasurement.logDate, this))
                                        val secondLastMeasurement = item.measurements.filter { m -> m.value != lastMeasurement.value }.maxBy { m -> m.logDate }
                                        if (secondLastMeasurement == null) {
                                            holder.itemView.changeDirection.visibility = View.INVISIBLE
                                            holder.itemView.changeAmount.visibility = View.GONE
                                        }
                                        else {
                                            holder.itemView.changeDirection.visibility = View.VISIBLE
                                            holder.itemView.changeAmount.visibility = View.VISIBLE
                                            val change = lastMeasurement.value - secondLastMeasurement.value
                                            holder.itemView.changeDirection.rotation = if (change < 0) 0f else 180f
                                            val days = Utils.getNrOfDaysPassed(lastMeasurement.logDate, secondLastMeasurement.logDate)
                                            holder.itemView.changeAmount.text = getString(R.string.change_in_days, abs(change), item.unit, days)
                                        }*/
                                    }
                                    holder.itemView.addMeasurementButton.setOnClickListener {
                                        val newMeasurement = Measurement(this)
                                        newMeasurement.parameter = item
                                        newMeasurement.parameterId = item.id
                                        PopupEditor(0, Measurement::class.java, this)
                                                .withExistingInstance(newMeasurement).show()
                                    }
                                }, R.drawable.ic_home_black_48dp)
                                .withAddNewElementButton(R.drawable.ic_assignment_black_48dp, R.string.add_parameter)
                                .build()
                            .addHistoryView(Measurement::class.java, { (viewModel as ViewModel).measurements }, R.layout.measurement_row_layout, { holder, item ->
                                    //holder.itemView.measurementValueTextView.text = String.format("%.1f %s", item.value, item.parameter.unit)
                                    holder.itemView.measurementDateTextView.text = Utils.getFormattedDateString(item.logDate, this)
                                })
                                .groupBy{ item -> (item as Measurement).parameter.name }
                                .build()
    }
}
