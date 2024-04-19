package com.boroscsaba.bodymeasurementtracker.view


import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.boroscsaba.bodymeasurementtracker.R
import com.boroscsaba.bodymeasurementtracker.model.MeasurementLogEntry
import com.boroscsaba.bodymeasurementtracker.model.Parameter
import com.boroscsaba.bodymeasurementtracker.technical.MeasurementHistoryRecyclerViewAdapter
import com.boroscsaba.bodymeasurementtracker.viewmodel.MainViewModel
import com.boroscsaba.commonlibrary.activities.FragmentBase
import com.boroscsaba.commonlibrary.activities.AdsDisplayOptions
import com.boroscsaba.commonlibrary.settings.SettingsHelper
import com.google.android.gms.ads.AdSize
import kotlinx.android.synthetic.main.fragment_measurement_history.*


class MeasurementHistoryView : FragmentBase(R.layout.fragment_measurement_history) {

    init {
        options.adsOptions = AdsDisplayOptions(AdSize.SMART_BANNER, R.id.AdContainerHistory, "ca-app-pub-7535188687645300/9436768792")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        }
        viewModel?.parameters?.observe(this, Observer { parameters -> onParametersChanged(parameters) })
    }

    private fun onParametersChanged(parameters: ArrayList<Parameter>?) {
        if (parameters != null) {
            val application = activity?.application ?: return
            val allMeasurements = ArrayList<MeasurementLogEntry>()
            parameters.forEach { parameter -> allMeasurements.addAll(parameter.measurements) }
            val groups = ArrayList(allMeasurements
                    .groupBy { measurement -> SettingsHelper(application).getDateFormat().format(measurement.logDate) }
                    .values)
            if (recyclerView.adapter == null) {
                recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
                val adapter = MeasurementHistoryRecyclerViewAdapter(activity as AppCompatActivity, groups)
                recyclerView.adapter = adapter
            }
            else {
                (recyclerView.adapter as MeasurementHistoryRecyclerViewAdapter).changeValues(groups)
                (recyclerView.adapter as MeasurementHistoryRecyclerViewAdapter).notifyDataSetChanged()
            }
        }
    }
}
