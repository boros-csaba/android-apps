package com.boroscsaba.myweightlosstracker.model

import android.content.Context
import com.boroscsaba.myweightlosstracker.R
import com.boroscsaba.dataaccess.IObjectWithId
import com.boroscsaba.dataaccess.PersistentObject
import com.boroscsaba.dataaccess.PropertyEditor
import com.boroscsaba.dataaccess.RepositoryBaseBase
import com.boroscsaba.myweightlosstracker.dataAccess.MeasurementRepository
import com.boroscsaba.commonlibrary.viewelements.popupEditor.DatePropertyEditor
import com.boroscsaba.commonlibrary.viewelements.popupEditor.EditorIconEnum
import com.boroscsaba.commonlibrary.viewelements.popupEditor.EditorValidationEnum
import com.boroscsaba.commonlibrary.viewelements.popupEditor.NumberPropertyEditor

class Measurement(override val context: Context): PersistentObject(), IObjectWithId {
	override var id: Int = 0
	override var createdDate: Long = 0
	override var modifiedDate: Long = 0
	override var guid: String = ""
	var metricValue: Double = 0.0
	var imperialValue1: Double = 0.0
	var imperialValue2: Double = 0.0
	var parameterId: Int = 0
	var logDate: Long = System.currentTimeMillis()
	var description: String = ""

	var parameter: Parameter = Parameter(context)

	override fun getNewTitle(): String { return parameter.name }

	override fun getRepository(): RepositoryBaseBase<PersistentObject>? {
		@Suppress("UNCHECKED_CAST")
		return MeasurementRepository(context) as RepositoryBaseBase<PersistentObject>
	}

	override fun onlyOneObjectADay(): Boolean { return true }
	override fun getDailyUniqueValue(): Int { return parameterId }
	override fun getEffectiveDate(): Long { return logDate }
    override fun getEditTitle(): String { return parameter.name }


	override fun getPropertyEditors(): ArrayList<PropertyEditor> {
		val editors = ArrayList<PropertyEditor>()
		//editors.add(NumberPropertyEditor("value", R.string.value, EditorIconEnum.NUMBER_TREND, listOf(EditorValidationEnum.CANNOT_BE_ZERO)) { item -> (item as Measurement).value })
		editors.add(DatePropertyEditor("log_date", R.string.date, EditorIconEnum.DATE, emptyList()) { item -> (item as Measurement).logDate })
		return editors
	}
}