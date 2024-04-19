package com.boroscsaba.commonlibrary.viewelements.popupEditor

import android.text.InputType
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.commonlibrary.helpers.ViewHelper
import com.boroscsaba.dataaccess.EntityBase
import com.google.android.material.textfield.TextInputLayout
import java.text.DecimalFormat

class NumberPropertyEditor(propertyName: String, labelResourceId: Int, iconEnum: EditorIconEnum?, validations: List<EditorValidationEnum>, getter: (EntityBase) -> Double, setter: (EntityBase, Double) -> Unit): TextInputLayoutPropertyEditor<Double>(propertyName, labelResourceId, iconEnum?.iconResourceId, validations.map { v -> v.validationTextResourceId }, getter, setter) {

    override fun validateInput(container: ViewGroup): Boolean {
        val editorView = ViewHelper.findViewByTagName(container, propertyName) as TextInputLayout
        val value = editorView.editText?.text?.toString()?.toDoubleOrNull() ?: 0.0
        for (validation in validations) {
            if (validation == EditorValidationEnum.MORE_THAN_ZERO.validationTextResourceId) {
                if (value <= 0.0) {
                    editorView.error = container.context.getString(validation)
                    return false
                }
                else editorView.error = null
            }
            else if (validation == EditorValidationEnum.CANNOT_BE_ZERO.validationTextResourceId) {
                if (Utils.round(value, 2) == 0.0) {
                    editorView.error = container.context.getString(validation)
                    return false
                }
                else editorView.error = null
            }
        }
        return true
    }

    override fun mapValue(entity: EntityBase, container: ViewGroup) {
        val editorView = ViewHelper.findViewByTagName(container, propertyName) as TextInputLayout
        val value = editorView.editText?.text?.toString()?.toDoubleOrNull() ?: 0.0
        setter(entity, value)
    }

    override fun createView(context: AppCompatActivity, liveData: LiveData<EntityBase>): View {
        val textInputLayout = createTextInputLayout(propertyName, labelResourceId, InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL, context)
        val format = DecimalFormat("0.##")
        liveData.observe(context, Observer { item ->
            if (item != null) {
                val textValue = format.format(getter(item))
                textInputLayout.editText?.setText(textValue)
                textInputLayout.setTag(R.id.tag_original_value, textValue)
            }
        })
        return textInputLayout
    }
}