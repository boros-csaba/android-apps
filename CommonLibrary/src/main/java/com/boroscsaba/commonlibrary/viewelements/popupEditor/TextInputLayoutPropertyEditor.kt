package com.boroscsaba.commonlibrary.viewelements.popupEditor

import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.dataaccess.EntityBase
import com.boroscsaba.dataaccess.PropertyEditor
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

abstract class TextInputLayoutPropertyEditor<T>(propertyName: String, labelResourceId: Int, iconResourceId: Int?, validations: List<Int>, getter: (EntityBase) -> T, setter: (EntityBase, T) -> Unit): PropertyEditor<T>(propertyName, labelResourceId, iconResourceId, validations, getter, setter) {

    internal fun createTextInputLayout(name: String, hintResourceId: Int, inputType: Int, context: AppCompatActivity): TextInputLayout {
        val textInputEditText = TextInputEditText(context)
        textInputEditText.setHint(hintResourceId)
        textInputEditText.inputType = inputType
        val textInputLayout = TextInputLayout(context)
        textInputLayout.id = View.generateViewId()
        textInputLayout.setTag(R.id.tag_name, name)
        textInputLayout.addView(textInputEditText)
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.marginEnd = Utils.convertDpToPixel(16f, context).toInt()
        textInputLayout.layoutParams = layoutParams
        return textInputLayout
    }
}