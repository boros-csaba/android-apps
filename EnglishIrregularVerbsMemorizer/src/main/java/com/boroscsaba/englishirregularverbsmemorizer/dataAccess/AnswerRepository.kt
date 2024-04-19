package com.boroscsaba.englishirregularverbsmemorizer.dataAccess

import android.content.Context
import com.boroscsaba.englishirregularverbsmemorizer.model.Answer

class AnswerRepository (context: Context) : RepositoryBase<Answer>(context, Answer::class.java, AnswerMapper(context))