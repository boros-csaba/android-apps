package com.boroscsaba.myapplication.dataAccess

import android.content.Context
import com.boroscsaba.commonlibrary.settings.SettingOption
import com.boroscsaba.commonlibrary.settings.SettingOptionMapper

class SettingOptionRepository(val context: Context) : RepositoryBase<SettingOption>(context, SettingOption::class.java, SettingOptionMapper(context))