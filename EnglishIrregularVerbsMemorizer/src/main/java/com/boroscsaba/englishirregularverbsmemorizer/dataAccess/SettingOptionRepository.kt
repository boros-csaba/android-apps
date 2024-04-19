package com.boroscsaba.englishirregularverbsmemorizer.dataAccess

import android.content.Context
import com.boroscsaba.commonlibrary.settings.SettingOption
import com.boroscsaba.commonlibrary.settings.SettingOptionMapper

class SettingOptionRepository(context: Context) : RepositoryBase<SettingOption>(context, SettingOption::class.java, SettingOptionMapper(context))