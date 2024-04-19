package com.boroscsaba.commonlibrary.settings

import android.content.Context
import com.boroscsaba.commonlibrary.R

class CurrencySetting(context: Context, val onlyText: Boolean): Setting(context, true, "CURRENCY_SETTING", 1.0, "", R.string.currency, R.string.currency_setting_description)