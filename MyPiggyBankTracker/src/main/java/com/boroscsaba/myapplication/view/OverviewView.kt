package com.boroscsaba.myapplication.view

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.activities.ActivityBase
import com.boroscsaba.commonlibrary.adapters.SimpleArrayAdapter
import com.boroscsaba.commonlibrary.viewelements.currency.CurrencyManager
import com.boroscsaba.myapplication.R
import kotlinx.android.synthetic.main.activity_overview.*
import com.boroscsaba.myapplication.technical.OverviewTabsPagerFragmentAdapter
import com.boroscsaba.myapplication.viewmodel.OverviewViewModel


class OverviewView : ActivityBase(), AdapterView.OnItemSelectedListener {

    init {
        options.layout = R.layout.activity_overview
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val themeManager = (application as ApplicationBase).themeManager
        viewModel = ViewModelProviders.of(this).get(OverviewViewModel::class.java)
        val viewModel = viewModel as OverviewViewModel
        viewModel.usedCurrencies.observe(this, Observer{ value ->
            if (value != null) {
                val dataAdapter = SimpleArrayAdapter(this, R.layout.currency_spinner_dropdown_item, ArrayList(value.map{ CurrencyManager.getCurrency(it)}), { view, item ->
                    view.setBackgroundColor(themeManager.getColor(com.boroscsaba.commonlibrary.ThemeManager.FOREGROUND_COLOR))
                    val titleTextView = view.findViewById<TextView>(R.id.currencyName)
                    val iconImage = view.findViewById<ImageView>(R.id.currencyImage)
                    if (item.iconResourceId == null) {
                        titleTextView.visibility = View.VISIBLE
                        titleTextView.text = item.currencyCode
                        titleTextView.setTextColor(Color.parseColor("#ffffff"))
                        iconImage.visibility = View.GONE
                    }
                    else {
                        titleTextView.visibility = View.GONE
                        iconImage.setImageResource(item.iconResourceId!!)
                        iconImage.visibility = View.VISIBLE
                    }
                }, { item -> item.currencyCode })
                currencySpinner.adapter = dataAdapter

                if (value.isNotEmpty()) {
                    viewModel.selectedCurrency.value = value.first()
                    currencySpinner.onItemSelectedListener = this
                }
            }
        })
        viewModel.initialize(intent)

        setupActionBar()
        val tabIndex = intent.getIntExtra("TabIndex", 0)
        setupTabLayout(tabIndex)
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupTabLayout(tabIndex: Int) {
        val adapter = OverviewTabsPagerFragmentAdapter(supportFragmentManager)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_insert_chart_white_48dp)
        tabLayout.getTabAt(0)?.icon?.setTint(Color.parseColor("#ffffff"))
        tabLayout.getTabAt(1)?.setIcon(R.mipmap.ic_check_white_48dp)
        tabLayout.getTabAt(1)?.icon?.setTint(Color.parseColor("#ffffff"))
        viewPager.currentItem = tabIndex
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val viewModel = viewModel as OverviewViewModel
        viewModel.selectedCurrency.value = viewModel.usedCurrencies.value!![position]
    }
}
