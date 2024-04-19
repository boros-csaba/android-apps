package com.boroscsaba.commonlibrary.settings

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.text.InputType
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.activities.ActivityBase
import kotlinx.android.synthetic.main.single_value_popup_edit_unit_selector_layout.view.*
import androidx.appcompat.view.ContextThemeWrapper
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.commonlibrary.adapters.SimpleArrayAdapter
import com.boroscsaba.commonlibrary.viewelements.currency.AmountWithCurrencyView
import com.boroscsaba.commonlibrary.viewelements.currency.Currency
import com.boroscsaba.commonlibrary.viewelements.currency.CurrencyManager
import kotlinx.android.synthetic.main.currency_setting_editor_layout.view.*
import kotlinx.android.synthetic.main.simple_popup_options_layout.view.*


class SettingsPopupHelper {

    companion object {

        private var dialog: AlertDialog? = null

        @SuppressLint("InflateParams")
        fun showSettingPopup(setting: Setting, activity: ActivityBase, cancellable: Boolean) {
            val builder = AlertDialog.Builder(ContextThemeWrapper(activity, R.style.AlertDialog))
            builder.setTitle(setting.titleResourceId)
            builder.setCancelable(cancellable)

            if (setting.descriptionResourceId != null) {
                val spannable = SpannableString(activity.getString(setting.descriptionResourceId))
                Linkify.addLinks(spannable, Linkify.ALL)
                builder.setMessage(spannable)
            }

            val settingOption = (activity.application as ApplicationBase).getSettingOptionRepository().getObjects("setting_name = ?", arrayOf(setting.name), null).first()
            val view: View
            when {
                setting.showValueEditor -> {
                    view = LayoutInflater.from(activity).inflate(R.layout.single_value_popup_edit_unit_selector_layout, null)
                    view.editText.setText(String.format("%.1f", settingOption.value))
                    view.editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                    val adapter = ArrayAdapter(activity, R.layout.simple_spinner_item, setting.unitArray)
                    val unit = settingOption.displayValue.split(' ').last()
                    view.unitSpinner.adapter = adapter
                    view.unitSpinner.setSelection(adapter.getPosition(unit))
                    builder.setPositiveButton(activity.getString(R.string.save)) { _, _ ->
                        val stringValue = view.editText.text.toString()
                        val value = Utils.toDoubleOrNull(stringValue) ?: 0.0
                        settingOption.value = value
                        settingOption.displayValue = String.format("%.1f %s", value, setting.defaultUnit)
                        saveSetting(setting, settingOption, activity)
                    }
                    builder.setNegativeButton(activity.getString(R.string.cancel)) { _, _ -> }
                }
                setting.showNumberPicker -> {
                    view = LayoutInflater.from(activity).inflate(R.layout.single_value_popup_edit_layout, null)
                    view.findViewById<TextView>(R.id.editText).visibility = View.GONE
                    view.findViewById<TextView>(R.id.unitText).text = setting.defaultUnit
                    val numberPicker = view.findViewById<NumberPicker>(R.id.numberPicker)
                    numberPicker.visibility = View.VISIBLE
                    numberPicker.isSaveFromParentEnabled = false
                    numberPicker.isSaveEnabled = true
                    numberPicker.minValue = setting.minValue ?: 0
                    numberPicker.maxValue = setting.maxValue ?: 1000
                    numberPicker.value = settingOption.value.toInt()
                    builder.setPositiveButton(activity.getString(R.string.save)) { _, _ ->
                        settingOption.value = numberPicker.value.toDouble()
                        settingOption.displayValue = "${numberPicker.value} ${setting.defaultUnit}"
                        saveSetting(setting, settingOption, activity)
                    }
                    builder.setNegativeButton(activity.getString(R.string.cancel)) { _, _ -> }
                }
                setting is CurrencySetting -> {
                    view = LayoutInflater.from(activity).inflate(R.layout.currency_setting_editor_layout, null)
                    val dataAdapter = SimpleArrayAdapter(activity, R.layout.currency_spinner_dropdown_item, CurrencyManager.getCurrencyList(setting.onlyText), { currencyView, item ->
                        val titleTextView = currencyView.findViewById<TextView>(R.id.currencyName)
                        val iconImage = currencyView.findViewById<ImageView>(R.id.currencyImage)
                        if (item.iconResourceId == null) {
                            titleTextView.visibility = View.VISIBLE
                            titleTextView.text = item.currencyCode
                            iconImage.visibility = View.GONE
                        }
                        else {
                            titleTextView.visibility = View.GONE
                            iconImage.setImageResource(item.iconResourceId)
                            iconImage.visibility = View.VISIBLE
                        }
                    }, { item -> item.currencyCode })
                    view.currencySpinner.adapter = dataAdapter
                    view.currencySpinner.setSelection(CurrencyManager.getCurrencyList(setting.onlyText).indexOfFirst { c -> c.id == settingOption.value.toInt() })
                    builder.setPositiveButton(activity.getString(R.string.save)) { _, _ ->
                        val selectedCurrency = view.currencySpinner.selectedItem as Currency
                        settingOption.value = selectedCurrency.id.toDouble()
                        saveSetting(setting, settingOption, activity)
                    }
                    builder.setNegativeButton(activity.getString(R.string.cancel)) { _, _ -> }
                }
                else -> {
                    view = LayoutInflater.from(activity).inflate(R.layout.simple_popup_options_layout, null)
                    if (setting.option1 != null) {
                        view.option1.visibility = View.VISIBLE
                        view.option1.text = setting.option1?.displayValue
                        view.option1.setOnClickListener {
                            saveSetting(setting, setting.option1, activity)
                            dialog?.dismiss()
                        }
                    }
                    if (setting.option2 != null) {
                        view.option2.visibility = View.VISIBLE
                        view.option2.text = setting.option2?.displayValue
                        view.option2.setOnClickListener {
                            saveSetting(setting, setting.option2, activity)
                            dialog?.dismiss()
                        }
                    }
                    if (setting.option3 != null) {
                        view.option3.visibility = View.VISIBLE
                        view.option3.text = setting.option3?.displayValue
                        view.option3.setOnClickListener {
                            saveSetting(setting, setting.option3, activity)
                            dialog?.dismiss()
                        }
                    }
                }
            }
            builder.setView(view)
            dialog = builder.show()
            dialog?.findViewById<TextView>(android.R.id.message)?.movementMethod = LinkMovementMethod.getInstance()
        }

        private fun saveSetting(setting: Setting, value: SettingOption?, context: ActivityBase) {
            if (value == null) return
            val app = context.application as ApplicationBase
            app.settings.first { s -> s.settingName == setting.name }.value = value.value
            app.settings.first { s -> s.settingName == setting.name }.displayValue = value.displayValue

            val settingOption = app.getSettingOptionRepository().getObjects("setting_name = ?", arrayOf(setting.name), null).first()
            settingOption.value = value.value
            settingOption.displayValue = value.displayValue
            app.getSettingOptionRepository().upsert(settingOption, false)

            val settingsContainer = context.findViewById<LinearLayout>(R.id.settings_container)
            if (settingsContainer != null) {
                val textView = settingsContainer.findViewWithTag<TextView>(setting.name)
                textView?.text = value.displayValue
            }
            if (setting is CurrencySetting) {
                val currency = CurrencyManager.getCurrency(settingOption.value.toInt())
                val currencyCodeTextView = settingsContainer.findViewWithTag<TextView>("currencyCode")
                val currencyText = currency.currencyCode + " - "
                currencyCodeTextView?.text = currencyText
                val amountWithCurrencyView = settingsContainer.findViewWithTag<AmountWithCurrencyView>("currencyValue")
                amountWithCurrencyView?.setup(1234.0, currency)
            }

            value.action?.invoke(context)
        }
    }
}