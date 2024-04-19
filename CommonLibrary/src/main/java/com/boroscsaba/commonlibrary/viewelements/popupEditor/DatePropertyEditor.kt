package com.boroscsaba.commonlibrary.viewelements.popupEditor

import android.app.DatePickerDialog
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.commonlibrary.helpers.ViewHelper
import com.boroscsaba.commonlibrary.views.TextView
import com.boroscsaba.dataaccess.EntityBase
import com.boroscsaba.dataaccess.PropertyEditor
import java.util.*

class DatePropertyEditor (propertyName: String, labelResourceId: Int, iconEnum: EditorIconEnum?, validations: List<EditorValidationEnum>, getter: (EntityBase) -> Long, setter: (EntityBase, Long) -> Unit): PropertyEditor<Long>(propertyName, labelResourceId, iconEnum?.iconResourceId, validations.map { v -> v.validationTextResourceId }, getter, setter) {

    override fun validateInput(container: ViewGroup): Boolean {
        return true
    }

    override fun mapValue(entity: EntityBase, container: ViewGroup) {
        val editorView = ViewHelper.findViewByTagName(container, propertyName)
        val value = editorView?.getTag(R.id.tag_value) as Long
        setter(entity, value)
    }

    override fun createView(context: AppCompatActivity, liveData: LiveData<EntityBase>): View {
        val textView = TextView(context)
        textView.textSize = 18f
        textView.setTag(R.id.tag_name, propertyName)
        liveData.observe(context, Observer { item ->
            if (item != null) {
                val value = getter(item)
                textView.text = Utils.getFormattedDateString(value, context)
                textView.setTag(R.id.tag_original_value, value)
                textView.setTag(R.id.tag_value, value)
            }
        })
        textView.setOnClickListener {
            val newCalendar = Calendar.getInstance()
            newCalendar.timeInMillis = textView.getTag(R.id.tag_value) as Long
            DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val newDate = Calendar.getInstance()
                newDate.set(year, monthOfYear, dayOfMonth)
                textView.setTag(R.id.tag_value, newDate.timeInMillis)
                textView.text = Utils.getFormattedDateString(newDate.timeInMillis, context)
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        return textView
    }
}