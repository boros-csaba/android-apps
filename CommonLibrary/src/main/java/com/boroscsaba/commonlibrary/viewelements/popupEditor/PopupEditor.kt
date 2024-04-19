package com.boroscsaba.commonlibrary.viewelements.popupEditor

import android.app.AlertDialog
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.boroscsaba.commonlibrary.AsyncTask
import com.boroscsaba.commonlibrary.IDao
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.commonlibrary.helpers.EntityHelper
import com.boroscsaba.commonlibrary.helpers.ViewHelper
import com.boroscsaba.commonlibrary.views.TextView
import com.boroscsaba.dataaccess.EntityBase
import com.boroscsaba.dataaccess.PropertyEditor
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.popup_editor_layout.*
import kotlin.collections.ArrayList


class PopupEditor<T: EntityBase>(private val classType: Class<T>, private val dao: IDao<T>, private val context: AppCompatActivity) {

    private var liveData: LiveData<T?>? = null
    private var existingId: Int? = null
    private var existingInstance: T? = null

    fun withExistingId(id: Int): PopupEditor<T> {
        this.existingId = id
        return this
    }

    fun withNewInstance(): PopupEditor<T> {
        this.existingInstance = EntityHelper.getNew(classType)
        return this
    }

    fun withExistingInstance(item: T): PopupEditor<T> {
        this.existingInstance = item
        return this
    }

    fun show() {
        if (existingInstance == null && existingId == null) throw IllegalArgumentException("Instance or Id must be provided for Popup Dialog!")

        val instance = EntityHelper.getNew(classType)
        val onlyOneObjectADay = instance.onlyOneObjectADay()
        val propertyEditors = instance.getPropertyEditors()

        if (existingInstance == null) {
            liveData = dao.getAsync(existingId!!)
        }
        else {
            val mutableLiveData = MutableLiveData<T>()
            mutableLiveData.value = existingInstance
            liveData = mutableLiveData
        }

        val dialog = AlertDialog.Builder(context, R.style.FullScreenDialogStyle).setView(R.layout.popup_editor_layout).create()
        dialog.setCancelable(false)
        dialog.show()
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        handleResizing(dialog)
        createLayout(dialog, propertyEditors)

        val id = existingId ?: 0
        if (id > 0) {
            dialog.deleteButton.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setMessage(context.getString(R.string.delete_entry_prompt))
                builder.setCancelable(true)
                builder.setPositiveButton(context.getString(R.string.delete)) { discardDialog, _ ->
                    discardDialog.cancel()
                    delete()
                    dialog.cancel()
                }
                builder.setNegativeButton(context.getString(R.string.cancel)) { discardDialog, _ -> discardDialog.cancel() }
                val alert = builder.create()
                alert.show()
            }
        }
        else {
            dialog.deleteButton.visibility = View.GONE
        }
        dialog.saveButton.setOnClickListener {
            if (save(dialog, propertyEditors, onlyOneObjectADay)) dialog.dismiss()
        }
        dialog.popupCloseButton.setOnClickListener {
            discardPopup(dialog, propertyEditors)
        }

        if (existingInstance == null) {
            liveData?.observe(context, Observer { item ->
                if (item != null) {
                    updatePopupFields(item, dialog)
                }
            })
        }
        else {
            updatePopupFields(existingInstance!!, dialog)
        }
    }

    private fun updatePopupFields(item: T, dialog: Dialog) {
        dialog.popupTitle.text = if (item.id == 0) item.getNewTitle(context) else item.getEditTitle(context)
    }

    private fun handleResizing(dialog: Dialog) {
        val listener = ViewTreeObserver.OnPreDrawListener { setSize(dialog) }
        context.window?.decorView?.viewTreeObserver?.addOnPreDrawListener(listener)
        dialog.setOnDismissListener { context.window?.decorView?.viewTreeObserver?.removeOnPreDrawListener(listener) }
        setSize(dialog)
    }

    private fun setSize(dialog: Dialog): Boolean {
        val displayFrame = Rect()
        context.window.decorView.getWindowVisibleDisplayFrame(displayFrame)
        val layoutParams = dialog.outerContainer.layoutParams
        layoutParams.height = displayFrame.height()
        dialog.outerContainer.requestLayout()
        return true
    }

    private fun createLayout(dialog: AlertDialog, propertyEditors: ArrayList<PropertyEditor<*>>) {
        val container = dialog.container
        for (editor in propertyEditors) {
            @Suppress("UNCHECKED_CAST") val editorView = editor.createView(context, liveData!! as LiveData<EntityBase>)
            val withLabel: Boolean = when (editor) {
                is NumberPropertyEditor -> false
                is SpinnerPropertyEditor -> true
                is DatePropertyEditor -> true
                else -> false
            }
            addEditorToContainer(container, editorView, editor.labelResourceId, editor.iconResourceId, withLabel)
        }
    }

    private fun addEditorToContainer(container: LinearLayout, editorView: View, labelResourceId: Int, iconResourceId: Int?, withLabel: Boolean) {
        if (iconResourceId == null) {
            container.addView(editorView)
        }
        else {
            val innerContainer = LinearLayout(context)
            innerContainer.orientation = LinearLayout.HORIZONTAL
            val iconImageView = ImageView(context)
            iconImageView.contentDescription = context.getString(labelResourceId)
            iconImageView.imageTintList = ColorStateList.valueOf(Color.parseColor("#999999"))
            val imageLayoutParams = LinearLayout.LayoutParams(Utils.convertDpToPixel(22f, context).toInt(), Utils.convertDpToPixel(22f, context).toInt())
            imageLayoutParams.marginStart = Utils.convertDpToPixel(16f, context).toInt()
            imageLayoutParams.marginEnd = Utils.convertDpToPixel(24f, context).toInt()
            imageLayoutParams.bottomMargin = Utils.convertDpToPixel(8f, context).toInt()
            imageLayoutParams.topMargin = Utils.convertDpToPixel(8f, context).toInt()
            imageLayoutParams.gravity = Gravity.BOTTOM
            iconImageView.layoutParams = imageLayoutParams
            Utils.setImageViewSource(iconResourceId, iconImageView, context)
            innerContainer.addView(iconImageView)
            if (withLabel) {
                val label = TextView(context)
                val labelText = "${context.getText(labelResourceId)}:"
                label.text = labelText
                label.setStyle("LIGHT")
                label.textSize = 18f
                val labelLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                labelLayoutParams.marginStart = Utils.convertDpToPixel(4f, context).toInt()
                labelLayoutParams.marginEnd = Utils.convertDpToPixel(16f, context).toInt()
                label.layoutParams = labelLayoutParams
                innerContainer.addView(label)
            }
            innerContainer.addView(editorView)
            container.addView(innerContainer)
        }
    }

    private fun discardPopup(dialog: Dialog, propertyEditors: ArrayList<PropertyEditor<*>>) {
        var hasChanges = false
        for (editor in propertyEditors) {
            val editorView = ViewHelper.findViewByTagName(dialog.container, editor.propertyName)
            val originalValue = editorView?.getTag(R.id.tag_original_value)
            hasChanges = when (editor) {
                is SpinnerPropertyEditor -> {
                    val spinner = editorView as Spinner
                    hasChanges || (originalValue != spinner.selectedItem)
                }
                is DatePropertyEditor -> {
                    val currentValue = editorView?.getTag(R.id.tag_value)
                    hasChanges || !Utils.areOnSameDay(originalValue as Long, currentValue as Long)
                }
                else -> {
                    val textInputLayout = editorView as TextInputLayout
                    hasChanges || (originalValue != textInputLayout.editText?.text?.toString())
                }
            }
        }
        if (hasChanges) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(context.getString(R.string.discard_changed))
            builder.setCancelable(true)
            builder.setPositiveButton(context.getString(R.string.discard)) { discardDialog, _ ->
                discardDialog.cancel()
                dialog.cancel()
            }
            builder.setNegativeButton(context.getString(R.string.cancel)) { discardDialog, _ -> discardDialog.cancel() }
            val alert = builder.create()
            alert.show()
        }
        else {
            dialog.cancel()
        }
    }

    private fun delete() {
        AsyncTask().execute({
            val entity = liveData?.value!!
            dao.delete(entity)
        })
    }

    private fun save(dialog: Dialog, propertyEditors: ArrayList<PropertyEditor<*>>, onlyOneObjectADay: Boolean): Boolean {
        val inputIsValid = propertyEditors.all { editor -> editor.validateInput(dialog.container) }
        if (inputIsValid) {
            propertyEditors.forEach { editor -> editor.mapValue(liveData?.value!!, dialog.container) }
            AsyncTask().execute({
                val entity = liveData?.value!!
                if (onlyOneObjectADay) {
                    val entities = dao.getAll()
                    entities.forEach { itemObject ->
                        if (itemObject.getDailyUniqueValue() == entity.getDailyUniqueValue() && Utils.areOnSameDay(itemObject.getEffectiveDate(), entity.getEffectiveDate())) {
                            entity.id = itemObject.id
                        }
                    }
                }
                if (entity.id == 0) dao.insert(entity)
                else dao.update(entity)
            })
        }
        return inputIsValid
    }


}