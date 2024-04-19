package com.boroscsaba.commonlibrary.activities.historyView

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.boroscsaba.commonlibrary.IDao
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.commonlibrary.activities.FragmentBase
import com.boroscsaba.dataaccess.EntityBase
import kotlinx.android.synthetic.main.base_layout_history_fragment.*


class HistoryFragment<T: EntityBase>: FragmentBase(R.layout.base_layout_history_fragment) {

    private var dataSource = MediatorLiveData<ArrayList<T>>()
    private var classType: Class<T>? = null
    private var dao: IDao<T>? = null
    private var components: List<Component<T>> = ArrayList()

    private var groupingFilter = MutableLiveData<String>()
    private var groupingSelector: ((T) -> String)? = null
    private var historyGroupSpinnerAdapter: ArrayAdapter<String>? = null
    private var historyGroupSpinnerListener: AdapterView.OnItemSelectedListener? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (classType == null) return
        setupDataChangeObserverInternal(classType!!, dao!!)
    }

    override fun onVisibilityChangedInViewPager(visible: Boolean) {
        super.onVisibilityChangedInViewPager(visible)
        if (historyGroupSpinnerAdapter == null) return
        val historyGroupSpinner = activity?.findViewById<Spinner>(R.id.historyGroupSpinner) ?: return
        historyGroupSpinner.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setup(classType: Class<T>, dao: IDao<T>, components: List<Component<*>>, liveData: LiveData<ArrayList<T>>, groupingSelector: ((T) -> String)?) {
        this.classType = classType
        this.dao = dao
        @Suppress("UNCHECKED_CAST")
        this.components = components as List<Component<T>>
        this.groupingSelector = groupingSelector
        dataSource.addSource(liveData) { dataSource.value = prepareData(liveData, groupingFilter, groupingSelector) }
        if (groupingSelector != null) {
            setupGroupSelector(groupingSelector, liveData.value ?: arrayListOf())
            dataSource.addSource(groupingFilter) { dataSource.value = prepareData(liveData, groupingFilter, groupingSelector) }
        }
    }

    private fun setupDataChangeObserverInternal(classType: Class<T>, dao: IDao<T>) {
        dataSource.observe(viewLifecycleOwner, Observer { objects ->
            if (objects != null) {
                objects.sortByDescending { o -> o.createdDate }
                if (historyRecyclerView.adapter == null) {
                    historyRecyclerView.layoutManager = LinearLayoutManager(context)
                    historyRecyclerView.adapter = HistoryRecyclerViewAdapter(components, objects, classType, dao, activity as AppCompatActivity)
                }
                else {
                    @Suppress("UNCHECKED_CAST")
                    val adapter = historyRecyclerView.adapter as HistoryRecyclerViewAdapter<T>
                    adapter.changeValues(objects)
                    adapter.notifyDataSetChanged()
                }
            }

            if (objects == null || objects.isEmpty()) {
                emptyStateContainer.visibility = View.VISIBLE
                @Suppress("UNCHECKED_CAST")
                val instance = classType.constructors.first().newInstance() as T
                emptyStateTitle.text = instance.getEmptyStateTitle(context!!)
                val icon = instance.getIcon()
                if (icon != null) {
                    Utils.setImageViewSource(instance.getIcon()!!, emptyStateIcon, context!!)
                }
                emptyStateDescription.text = instance.getEmptyStateDescription(context!!)
            }
            else {
                emptyStateContainer.visibility = View.GONE
            }
        })
    }

    private fun prepareData(liveData: LiveData<ArrayList<T>>, groupingFilter: LiveData<String>, groupingSelector: ((T) -> String)?): ArrayList<T> {
        val data = liveData.value ?: return arrayListOf()
        if (groupingSelector != null) {
            setupGroupSelector(groupingSelector, data)
            val filter = groupingFilter.value
            if (filter != null) {
                return ArrayList(data.filter { item -> groupingSelector(item) == filter })
            }
        }
        return data
    }

    private fun setupGroupSelector(selector: ((T) -> String)?, objects: ArrayList<T>) {
        if (selector == null) return
        val historyGroupSpinner = activity?.findViewById<Spinner>(R.id.historyGroupSpinner) ?: return
        val groups = ArrayList(objects.map { item -> selector(item) }.distinct() )
        if (historyGroupSpinner.adapter == null) {
            historyGroupSpinnerAdapter = ArrayAdapter(context!!, R.layout.simple_spinner_item_selected_white, groups)
            historyGroupSpinnerAdapter?.setDropDownViewResource(R.layout.simple_spinner_item)
            historyGroupSpinner.adapter = historyGroupSpinnerAdapter
            historyGroupSpinnerListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, itemView: View?, position: Int, id: Long) {
                    groupingFilter.value = historyGroupSpinnerAdapter?.getItem(position)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            historyGroupSpinner.onItemSelectedListener = historyGroupSpinnerListener
        }
        else {
            historyGroupSpinnerAdapter?.clear()
            historyGroupSpinnerAdapter?.addAll(groups)
            historyGroupSpinnerAdapter?.notifyDataSetChanged()
        }
    }
}