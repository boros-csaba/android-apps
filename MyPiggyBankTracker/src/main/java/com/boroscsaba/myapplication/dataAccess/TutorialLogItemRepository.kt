package com.boroscsaba.myapplication.dataAccess

import android.content.Context
import com.boroscsaba.commonlibrary.tutorial.TutorialLogItem
import com.boroscsaba.commonlibrary.tutorial.TutorialLogItemMapper

class TutorialLogItemRepository(val context: Context) : RepositoryBase<TutorialLogItem>(context, TutorialLogItem::class.java, TutorialLogItemMapper(context))