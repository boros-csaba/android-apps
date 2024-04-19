package com.boroscsaba.bodymeasurementtracker.model

import com.boroscsaba.bodymeasurementtracker.MeasurementPresetTypeEnum
import com.boroscsaba.dataaccess.IObjectWithId

class DashboardSettingsItem(override var id: Int, val title: String, val type: DashboardBlockTypeEnum, val enabledInitialValue: Boolean, var enabled: Boolean, val orderInitialValue: Int, var order: Int, val letterText: String, val presetType: MeasurementPresetTypeEnum, val color: String, val parameter: Parameter): IObjectWithId