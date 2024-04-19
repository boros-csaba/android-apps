package com.boroscsaba.bodymeasurementtracker.dataaccess

import android.content.Context
import com.boroscsaba.bodymeasurementtracker.model.Parameter
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.commonlibrary.helpers.SingletonHolder

/**
 * Created by Boros Csaba
 */
class ParameterRepository(val context: Context) : RepositoryBase<Parameter>(context, Parameter::class.java, ParameterMapper(context)) {

    override fun upsert(persistentObject: Parameter, alreadySynced: Boolean) : Int {
        if (persistentObject.id == 0) {
            LoggingHelper.logEvent(context, "New_Parameter","Name", persistentObject.name)
        }
        return super.upsert(persistentObject, alreadySynced)
    }

    companion object : SingletonHolder<ParameterRepository, Context>(::ParameterRepository)
}