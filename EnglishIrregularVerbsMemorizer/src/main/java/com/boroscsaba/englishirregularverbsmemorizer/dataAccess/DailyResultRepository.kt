package com.boroscsaba.englishirregularverbsmemorizer.dataAccess

import android.content.Context
import com.boroscsaba.englishirregularverbsmemorizer.model.DailyResult

class DailyResultRepository (context: Context) : RepositoryBase<DailyResult>(context, DailyResult::class.java, DailyResultMapper(context))