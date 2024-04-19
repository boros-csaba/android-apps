package com.boroscsaba.bodymeasurementsandweightlosstracker.model

import android.content.Context
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.boroscsaba.bodymeasurementsandweightlosstracker.R
import com.boroscsaba.commonlibrary.viewelements.popupEditor.DatePropertyEditor
import com.boroscsaba.commonlibrary.viewelements.popupEditor.EditorIconEnum
import com.boroscsaba.commonlibrary.viewelements.popupEditor.EditorValidationEnum
import com.boroscsaba.commonlibrary.viewelements.popupEditor.NumberPropertyEditor
import com.boroscsaba.dataaccess.EntityBase
import com.boroscsaba.dataaccess.IObjectWithId
import com.boroscsaba.dataaccess.PropertyEditor

@Entity(tableName = "Measurements",
		foreignKeys = [ForeignKey(entity = Parameter::class, parentColumns = ["id"], childColumns = ["parameter_id"], onDelete = CASCADE)])
open class Measurement : EntityBase(), IObjectWithId {
	@PrimaryKey(autoGenerate = true) override var id: Int = 0
	@ColumnInfo(name = "created_date") override var createdDate: Long = 0
	@ColumnInfo(name = "modified_date") override var modifiedDate: Long = 0
	@ColumnInfo(name = "guid") override var guid: String = ""
	@ColumnInfo(name = "value") var value: Double = 0.0
	@ColumnInfo(name = "parameter_id") var parameterId: Int = 0
	@ColumnInfo(name = "log_date") var logDate: Long = System.currentTimeMillis()

	@Ignore var parameter: Parameter? = null

	fun withParameter(parameter: Parameter?): Measurement {
		this.parameter = parameter
		return this
	}

	fun withParameter(parameters: List<Parameter>): Measurement {
		this.parameter = parameters.firstOrNull { p -> p.id == this.parameterId }
		return this
	}

	override fun getNewTitle(context: Context): String { return parameter?.name ?: "" }
	override fun onlyOneObjectADay(): Boolean { return true }
	override fun getDailyUniqueValue(): Int { return parameterId }
	override fun getEffectiveDate(): Long { return logDate }
    override fun getEditTitle(context: Context): String { return parameter?.name ?: "" }

	override fun getPropertyEditors(): ArrayList<PropertyEditor<*>> {
		val editors = ArrayList<PropertyEditor<*>>()
		editors.add(NumberPropertyEditor("value", R.string.value, EditorIconEnum.NUMBER_TREND, listOf(EditorValidationEnum.CANNOT_BE_ZERO), { item -> (item as Measurement).value }, { item, value -> (item as Measurement).value = value }))
		editors.add(DatePropertyEditor("log_date", R.string.date, EditorIconEnum.DATE, emptyList(), { item -> (item as Measurement).logDate }, { item, value -> (item as Measurement).logDate = value}))
		return editors
	}
}