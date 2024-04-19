package com.boroscsaba.bodymeasurementsandweightlosstracker.model

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.boroscsaba.bodymeasurementsandweightlosstracker.R
import com.boroscsaba.dataaccess.IObjectWithId
import com.boroscsaba.dataaccess.PropertyEditor
import com.boroscsaba.commonlibrary.viewelements.popupEditor.EditorIconEnum
import com.boroscsaba.commonlibrary.viewelements.popupEditor.EditorValidationEnum
import com.boroscsaba.commonlibrary.viewelements.popupEditor.SpinnerPropertyEditor
import com.boroscsaba.commonlibrary.viewelements.popupEditor.TextPropertyEditor
import com.boroscsaba.dataaccess.EntityBase

@Entity
class Parameter: EntityBase(), IObjectWithId {
	@PrimaryKey(autoGenerate = true) override var id: Int = 0
	@ColumnInfo(name = "created_date") override var createdDate: Long = 0
	@ColumnInfo(name = "modified_date") override var modifiedDate: Long = 0
    @ColumnInfo(name = "guid") override var guid: String = ""
	@ColumnInfo(name = "name") var name: String = ""
	@ColumnInfo(name = "goal_value") var goalValue: Double = 0.0
	@ColumnInfo(name = "unit") var unit: String = ""

	@Ignore var measurements: List<Measurement> = ArrayList()

	fun withMeasurements(measurements: List<Measurement>): Parameter {
		this.measurements = measurements.filter { measurement -> measurement.parameterId == id }.toList()
		return this
	}

	init {
		unit = "in"
	}

	override fun getNewTitle(context: Context): String { return context.getString(R.string.add_parameter) }
	override fun getEditTitle(context: Context): String { return name }

	override fun getPropertyEditors(): ArrayList<PropertyEditor<*>> {
		val editors = ArrayList<PropertyEditor<*>>()
		editors.add(TextPropertyEditor("name", R.string.name, EditorIconEnum.TITLE, listOf(EditorValidationEnum.AT_LEAST_3_CHARACTERS), { item -> (item as Parameter).name }, { item, value -> (item as Parameter).name = value }))
        val units = arrayOf("in", "cm", "lbs", "kg", "st", "%", "")
		editors.add(SpinnerPropertyEditor("unit", R.string.unit, units, EditorIconEnum.UNIT, emptyList(), { item -> (item as Parameter).unit }, { item, value -> (item as Parameter).unit = value }))
		return editors
	}
}