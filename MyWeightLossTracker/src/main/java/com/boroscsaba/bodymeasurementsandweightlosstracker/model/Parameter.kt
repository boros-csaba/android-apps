package com.boroscsaba.myweightlosstracker.model

import android.content.Context
import com.boroscsaba.myweightlosstracker.R
import com.boroscsaba.dataaccess.IObjectWithId
import com.boroscsaba.dataaccess.PersistentObject
import com.boroscsaba.dataaccess.PropertyEditor
import com.boroscsaba.dataaccess.RepositoryBaseBase
import com.boroscsaba.myweightlosstracker.dataAccess.ParameterRepository
import com.boroscsaba.commonlibrary.viewelements.popupEditor.EditorIconEnum
import com.boroscsaba.commonlibrary.viewelements.popupEditor.EditorValidationEnum
import com.boroscsaba.commonlibrary.viewelements.popupEditor.SpinnerPropertyEditor
import com.boroscsaba.commonlibrary.viewelements.popupEditor.TextPropertyEditor

class Parameter(override val context: Context): PersistentObject(), IObjectWithId {
	override var id: Int = 0
	override var createdDate: Long = 0
	override var modifiedDate: Long = 0
	override var guid: String = ""
	var name: String = ""
	var goalValue: Double = 0.0
	var unit: String = ""

	val measurements = ArrayList<Measurement>()

	init {
		unit = "in"
	}

	override fun getRepository(): RepositoryBaseBase<PersistentObject>? {
		@Suppress("UNCHECKED_CAST")
		return ParameterRepository(context) as RepositoryBaseBase<PersistentObject>
	}

	override fun getNewTitle(): String { return context.getString(R.string.add_parameter) }
	override fun getEditTitle(): String { return name }

	override fun getPropertyEditors(): ArrayList<PropertyEditor> {
		val editors = ArrayList<PropertyEditor>()
		editors.add(TextPropertyEditor("name", R.string.name, EditorIconEnum.TITLE, listOf(EditorValidationEnum.AT_LEAST_3_CHARACTERS)) { item -> (item as Parameter).name })
        val units = arrayOf("in", "cm", "lbs", "kg", "st", "%", "")
		editors.add(SpinnerPropertyEditor("unit", R.string.unit, units, EditorIconEnum.UNIT, emptyList()) { item -> (item as Parameter).unit })
		return editors
	}
}