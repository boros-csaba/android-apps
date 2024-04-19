package com.boroscsaba.commonlibrary.settings

import android.os.Bundle
import androidx.appcompat.widget.LinearLayoutCompat
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.activities.ActivityBase
import kotlinx.android.synthetic.main.activity_settings.*
import android.widget.LinearLayout
import android.util.TypedValue
import com.boroscsaba.commonlibrary.ThemeManager
import com.boroscsaba.commonlibrary.viewelements.currency.AmountWithCurrencyView
import com.boroscsaba.commonlibrary.viewelements.currency.CurrencyManager
import com.boroscsaba.commonlibrary.views.TextView


class SettingsActivity : ActivityBase() {

    init {
        options.layout = R.layout.activity_settings
        options.toolbarId = R.id.toolbar
        options.canShowAdConsentPopup = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.title = getString(R.string.navigation_drawer_settings)

        initializeSettings()
    }

    override fun setListeners() {}

    private fun initializeSettings() {
        val settings = (application as ApplicationBase).settingsConfigurations
        settings.filter{ s -> s.visible }.forEach { setting ->
            val innerContainer = LinearLayout(this)
            innerContainer.orientation = LinearLayout.VERTICAL
            innerContainer.isClickable = true
            innerContainer.isFocusable = true
            innerContainer.setOnClickListener { SettingsPopupHelper.showSettingPopup(setting, this, true) }
            val params = LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT)
            params.setMargins(0, 0, 0, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics).toInt())
            innerContainer.layoutParams = params

            val titleTextView = TextView(this)
            titleTextView.text = getString(setting.titleResourceId)
            titleTextView.textSize = 16f
            innerContainer.addView(titleTextView)

            if (setting is CurrencySetting) {
                val currencyId = SettingsHelper(application).getValue(setting.name)?.toInt() ?: 0
                val currency = CurrencyManager.getCurrency(currencyId)
                val valueContainer = LinearLayout(this)
                valueContainer.orientation = LinearLayout.HORIZONTAL
                val currencyNameTextView = TextView(this)
                currencyNameTextView.tag = "currencyCode"
                val currencyText = currency.currencyCode + " - "
                currencyNameTextView.text = currencyText
                valueContainer.addView(currencyNameTextView)
                val amountWithCurrencyView = AmountWithCurrencyView(this)
                amountWithCurrencyView.tag = "currencyValue"
                amountWithCurrencyView.setTextSize(45f)
                amountWithCurrencyView.setup(1234.0, currency)
                valueContainer.addView(amountWithCurrencyView)
                innerContainer.addView(valueContainer)
            }
            else {
                val valueTextView = TextView(this)
                valueTextView.text = SettingsHelper(application).getDisplayValue(setting.name)
                valueTextView.setTextColorName(ThemeManager.SECONDARY_TEXT_COLOR)
                valueTextView.tag = setting.name
                innerContainer.addView(valueTextView)
            }

            settings_container.addView(innerContainer)
        }
    }
}
