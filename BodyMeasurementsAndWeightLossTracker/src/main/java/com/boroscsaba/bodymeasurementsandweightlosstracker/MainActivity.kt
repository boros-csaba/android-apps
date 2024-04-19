package com.boroscsaba.bodymeasurementsandweightlosstracker

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.boroscsaba.bodymeasurementsandweightlosstracker.model.AppDatabase
import com.boroscsaba.bodymeasurementsandweightlosstracker.model.Measurement
import com.boroscsaba.commonlibrary.activities.ActivityDrawerBase
import com.boroscsaba.bodymeasurementsandweightlosstracker.model.Parameter
import com.boroscsaba.commonlibrary.IDao
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.commonlibrary.viewelements.charts.ChartData
import com.boroscsaba.commonlibrary.viewelements.popupEditor.PopupEditor
import kotlinx.android.synthetic.main.measurement_row_layout.view.*
import kotlinx.android.synthetic.main.parameter_row_layout.view.*
import kotlin.math.abs

class MainActivity : ActivityDrawerBase(R.id.drawerLayout, R.mipmap.ic_launcher) {

    init {
        val database = AppDatabase.getDatabase(this)
        options.withViewModel(ViewModel::class.java)
                    .withNavigationDrawer()
                        .withViewPager()
                            .withBottomTabsLayout()
                            .addHistoryView(Parameter::class.java, database.parameterDao(), { (viewModel as ViewModel).parameters }, R.drawable.ic_home_black_48dp)
                                .withListOfElements(R.layout.parameter_row_layout, getParameterListOnBindingViewHolder(database))
                                .withAddNewElementButton(R.drawable.ic_assignment_black_48dp, R.string.add_parameter)
                                .build()
                            .addHistoryView(Measurement::class.java, database.measurementDao(), { (viewModel as ViewModel).measurements })
                                .withLineChart { item -> ChartData(item.logDate, item.value) }
                                .withListOfElements(R.layout.measurement_row_layout, getMeasurementListOnBindViewHolder())
                                .groupBy{ item -> (item as Measurement).parameter?.name ?: "" }
                                .build()
    }

    private fun getParameterListOnBindingViewHolder(database: AppDatabase): (RecyclerView.ViewHolder, Parameter?, classType: Class<Parameter>, dao: IDao<Parameter>, activity: AppCompatActivity, items: List<Parameter>) -> Unit {
        return { holder: RecyclerView.ViewHolder, item: Parameter?, _, _, activity, _ ->
            if (item != null) {
                holder.itemView.parameterNameTextView.text = item.name
                val lastMeasurement = item.measurements.maxBy { m -> m.logDate }
                if (lastMeasurement == null) {
                    holder.itemView.changeDirection.visibility = View.INVISIBLE
                    holder.itemView.changeAmount.visibility = View.GONE
                    holder.itemView.parameterValue.visibility = View.GONE
                    holder.itemView.dateTextView.visibility = View.GONE
                    holder.itemView.addFirstMeasurementLabel.visibility = View.VISIBLE
                    holder.itemView.tutorialArrow.visibility = View.VISIBLE
                } else {
                    holder.itemView.parameterValue.visibility = View.VISIBLE
                    holder.itemView.dateTextView.visibility = View.VISIBLE
                    holder.itemView.addFirstMeasurementLabel.visibility = View.GONE
                    holder.itemView.tutorialArrow.visibility = View.GONE
                    val valueText = String.format("%.1f ", lastMeasurement.value) + item.unit
                    holder.itemView.parameterValue.text = valueText
                    holder.itemView.dateTextView.text = getString(R.string.on_date, Utils.getFormattedDateString(lastMeasurement.logDate, this))
                    val secondLastMeasurement = item.measurements.filter { m -> m.value != lastMeasurement.value }.maxBy { m -> m.logDate }
                    if (secondLastMeasurement == null) {
                        holder.itemView.changeDirection.visibility = View.INVISIBLE
                        holder.itemView.changeAmount.visibility = View.GONE
                    } else {
                        holder.itemView.changeDirection.visibility = View.VISIBLE
                        holder.itemView.changeAmount.visibility = View.VISIBLE
                        val change = lastMeasurement.value - secondLastMeasurement.value
                        holder.itemView.changeDirection.rotation = if (change < 0) 0f else 180f
                        val days = Utils.getNrOfDaysPassed(lastMeasurement.logDate, secondLastMeasurement.logDate)
                        holder.itemView.changeAmount.text = getString(R.string.change_in_days, abs(change), item.unit, days)
                    }
                }
                holder.itemView.addMeasurementButton.setOnClickListener {
                    val newMeasurement = Measurement()
                    newMeasurement.parameterId = item.id
                    newMeasurement.parameter = item
                    PopupEditor(Measurement::class.java, database.measurementDao(), activity)
                            .withExistingInstance(newMeasurement)
                            .show()
                }
            }
        }
    }

    private fun getMeasurementListOnBindViewHolder(): (RecyclerView.ViewHolder, Measurement?, classType: Class<Measurement>, dao: IDao<Measurement>, activity: AppCompatActivity, items: List<Measurement>) -> Unit {
        return { holder, item, _, _, _, _ ->
            holder.itemView.measurementValueTextView.text = String.format("%.1f %s", item?.value, item?.parameter?.unit)
            holder.itemView.measurementDateTextView.text = Utils.getFormattedDateString(item?.logDate ?: 0, this)
        }
    }
}
