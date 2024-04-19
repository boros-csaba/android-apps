package com.boroscsaba.myweightlosstracker.dataAccess

import android.content.Context
import com.boroscsaba.myweightlosstracker.model.Parameter

class ParameterRepository(context: Context) : RepositoryBase<Parameter>(context, Parameter::class.java, ParameterMapper(context))
