package com.boroscsaba.commonlibrary.viewelements.popupEditor

import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.commonlibrary.helpers.ViewHelper
import com.boroscsaba.dataaccess.EntityBase
import com.boroscsaba.dataaccess.PropertyEditor

class SpinnerPropertyEditor(propertyName: String, labelResourceId: Int, private val elements: Array<String>, iconEnum: EditorIconEnum?, validations: List<EditorValidationEnum>, getter: (EntityBase) -> String, setter: (EntityBase, String) -> Unit): PropertyEditor<String>(propertyName, labelResourceId, iconEnum?.iconResourceId, validations.map { v -> v.validationTextResourceId }, getter, setter) {

    override fun validateInput(container: ViewGroup): Boolean {
        return true
    }

    override fun mapValue(entity: EntityBase, container: ViewGroup) {
        val editorView = ViewHelper.findViewByTagName(container, propertyName) as Spinner
        val value = editorView.selectedItem as String
        setter(entity, value)
    }

    override fun createView(context: AppCompatActivity, liveData: LiveData<EntityBase>): View {
        val spinner = Spinner(context)
        spinner.id = View.generateViewId()
        spinner.setTag(R.id.tag_name, propertyName)
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.marginEnd = Utils.convertDpToPixel(16f, context).toInt()
        spinner.layoutParams = layoutParams
        spinner.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
        val padding = Utils.convertDpToPixel(4f, context).toInt()
        spinner.setPadding(0, padding, 0, padding)
        val adapter = ArrayAdapter(context, R.layout.simple_spinner_item_selected, elements)
        adapter.setDropDownViewResource(R.layout.simple_spinner_item)
        spinner.adapter = adapter
        liveData.observe(context, Observer { item ->
            if (item != null) {
                val textValue = getter(item)
                spinner.setSelection(adapter.getPosition(textValue))
                spinner.setTag(R.id.tag_original_value, textValue)
            }
        })
        return spinner
    }
}