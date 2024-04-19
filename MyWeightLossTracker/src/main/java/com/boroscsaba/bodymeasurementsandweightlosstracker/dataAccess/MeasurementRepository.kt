package com.boroscsaba.myweightlosstracker.dataAccess

import android.content.Context
import com.boroscsaba.myweightlosstracker.model.Measurement

class MeasurementRepository(context: Context) : RepositoryBase<Measurement>(context, Measurement::class.java, MeasurementMapper(context))
