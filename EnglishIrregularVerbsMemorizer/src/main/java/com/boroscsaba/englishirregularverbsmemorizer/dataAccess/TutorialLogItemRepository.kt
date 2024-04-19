package com.boroscsaba.englishirregularverbsmemorizer.dataAccess

import android.content.Context
import com.boroscsaba.commonlibrary.tutorial.TutorialLogItem
import com.boroscsaba.commonlibrary.tutorial.TutorialLogItemMapper

class TutorialLogItemRepository(context: Context) : RepositoryBase<TutorialLogItem>(context, TutorialLogItem::class.java, TutorialLogItemMapper(context))