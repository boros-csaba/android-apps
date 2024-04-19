package com.boroscsaba.bodymeasurementtracker.model

import android.content.Context
import com.boroscsaba.bodymeasurementtracker.MeasurementPresetTypeEnum
import com.boroscsaba.bodymeasurementtracker.dataaccess.ParameterRepository
import com.boroscsaba.dataaccess.IObjectWithId
import com.boroscsaba.dataaccess.PersistentObject
import com.boroscsaba.dataaccess.RepositoryBaseBase

/**
 * Created by Boros Csaba
 */

open class Parameter(override val context: Context) : PersistentObject(), IObjectWithId {
    override var id : Int = 0
    override var createdDate: Long = 0
    override var modifiedDate: Long = 0
    override var guid = ""
    var name : String = ""
    var color : String = "#0000ff"
    var unit: String = ""
    var standardIcon = 0
    var presetType = MeasurementPresetTypeEnum.CUSTOM
    var dashboardOrder = 0
    var dashboardEnabled = true
    var targetValue: Double = -1.0
    var targetDueDate: Long = 0

    val measurements = ArrayList<MeasurementLogEntry>()

    override fun getRepository(): RepositoryBaseBase<PersistentObject>? {
        @Suppress("UNCHECKED_CAST")
        return ParameterRepository(context) as RepositoryBaseBase<PersistentObject>
    }
}
