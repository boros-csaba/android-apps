package com.boroscsaba.commonlibrary.viewelements.popupEditor

import android.text.InputType
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.helpers.ViewHelper
import com.boroscsaba.dataaccess.EntityBase
import com.google.android.material.textfield.TextInputLayout

class TextPropertyEditor(propertyName: String, labelResourceId: Int, iconEnum: EditorIconEnum?, validations: List<EditorValidationEnum>, getter: (Any) -> String, setter: (EntityBase, String) -> Unit): TextInputLayoutPropertyEditor<String>(propertyName, labelResourceId, iconEnum?.iconResourceId, validations.map { v -> v.validationTextResourceId }, getter, setter) {

    override fun validateInput(container: ViewGroup): Boolean {
        val editorView = ViewHelper.findViewByTagName(container, propertyName) as TextInputLayout
        val value = editorView.editText?.text?.toString() ?: ""
        for (validation in validations) {
            if (validation == EditorValidationEnum.AT_LEAST_3_CHARACTERS.validationTextResourceId) {
                if (value.length < 3) {
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
        val value = editorView.editText?.text?.toString() ?: ""
        setter(entity, value)
    }

    override fun createView(context: AppCompatActivity, liveData: LiveData<EntityBase>): View {
        val textInputLayout = createTextInputLayout(propertyName, labelResourceId, InputType.TYPE_CLASS_TEXT, context)
        liveData.observe(context, Observer { item ->
            if (item != null) {
                val textValue = getter(item)
                textInputLayout.editText?.setText(textValue)
                textInputLayout.setTag(R.id.tag_original_value, textValue)
            }
        })
        return textInputLayout
    }
}