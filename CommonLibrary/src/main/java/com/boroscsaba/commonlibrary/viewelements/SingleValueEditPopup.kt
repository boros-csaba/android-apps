package com.boroscsaba.commonlibrary.viewelements

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AlertDialog
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.view.ContextThemeWrapper
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.Utils
import kotlinx.android.synthetic.main.single_value_popup_edit_layout.view.*
import java.util.*

class SingleValueEditPopup(private val context: Context?) {

    private var title: String = ""
    private var type = Type.Double
    private var onSaveDoubleFunction: ((value: Double) -> Unit)? = null
    private var onSaveIntFunction: ((value: Int) -> Unit)? = null
    private var onSaveTimeFunction: ((value: Long) -> Unit)? = null
    private var onSaveHoursMinutesFunction: ((hours: Int, minutes: Int) -> Unit)? = null
    private var doubleValue: Double? = null
    private var longValue: Long? = null
    private var intValue: Int? = null
    private var intValue2: Int? = null
    private var unit: String = ""

    fun withTitle(title: String): SingleValueEditPopup {
        this.title = title
        return this
    }

    fun withValue(value: Double?): SingleValueEditPopup {
        this.doubleValue = value
        type = Type.Double
        return this
    }

    fun withValue(value: Long?, type: Type): SingleValueEditPopup {
        this.longValue = value
        this.type = type
        return this
    }

    fun withValue(hours: Int, minutes: Int): SingleValueEditPopup {
        this.intValue = hours
        this.intValue2 = minutes
        this.type = Type.HoursMinutes
        return this
    }

    fun withUnit(unit: String): SingleValueEditPopup {
        this.unit = unit
        return this
    }

    fun onSaveDouble(onSaveFunction: ((value: Double) -> Unit)): SingleValueEditPopup {
        this.onSaveDoubleFunction = onSaveFunction
        return this
    }

    fun onSaveDateTime(onSaveFunction: ((value: Long) -> Unit)): SingleValueEditPopup {
        this.onSaveTimeFunction = onSaveFunction
        return this
    }

    fun onSaveHoursMinutes(onSaveHoursMinutesFunction: ((hours: Int, minutes: Int) -> Unit)): SingleValueEditPopup {
        this.onSaveHoursMinutesFunction = onSaveHoursMinutesFunction
        return this
    }

    fun show() {
        val context = context ?: return
        @SuppressLint("InflateParams")
        val view = LayoutInflater.from(context).inflate(R.layout.single_value_popup_edit_layout, null) as LinearLayout
        when (type) {
            Type.Double -> {
                view.editText.setText(String.format("%.1f", doubleValue ?: 0.0))
                view.editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            }
            Type.Integer -> {
                view.editText.setText((intValue ?: 0).toString())
                view.editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
            Type.DateTime -> {
                view.editText.visibility = View.GONE
                view.editTime.visibility = View.VISIBLE
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = longValue ?: 0
                view.editTime.currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                view.editTime.currentMinute = calendar.get(Calendar.MINUTE)
            }
            Type.HoursMinutes -> {
                view.editText.visibility = View.GONE
                view.hoursMinutesContainer.visibility = View.VISIBLE
                view.hoursPicker.isSaveFromParentEnabled = false
                view.hoursPicker.isSaveEnabled = true
                view.minutesPicker.isSaveFromParentEnabled = false
                view.minutesPicker.isSaveEnabled = true
                view.hoursPicker.minValue = 1
                view.hoursPicker?.maxValue = 11
                view.hoursPicker?.value = intValue ?: 0
                view.minutesPicker?.minValue = 0
                view.minutesPicker?.maxValue = 59
                view.minutesPicker?.value = intValue2 ?: 0
            }
        }
        view.unitText.text = unit

        val alertDialog = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialog))
                .setTitle(title)
                .setPositiveButton(context.getString(R.string.save)) { _, _ ->
                    val stringValue = view.editText.text.toString()
                    when (type) {
                        Type.Double -> {
                            val value = Utils.toDoubleOrNull(stringValue) ?: 0.0
                            onSaveDoubleFunction?.invoke(value)
                        }
                        Type.Integer -> {
                            val value = stringValue.toIntOrNull() ?: 0
                            onSaveIntFunction?.invoke(value)
                        }
                        Type.DateTime -> {
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = longValue ?: 0
                            val currentMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
                            val newMinutes = view.editTime.currentHour * 60 + view.editTime.currentMinute
                            val value = (longValue ?: 0).toLong() + (newMinutes - currentMinutes) * 60 * 1000
                            onSaveTimeFunction?.invoke(value)
                        }
                        Type.HoursMinutes -> {
                            onSaveHoursMinutesFunction?.invoke(view.hoursPicker.value, view.minutesPicker.value)
                        }
                    }
                }
                .setNegativeButton(context.getString(R.string.cancel)) { _, _ -> }
                .setView(view)
                .setCancelable(false)
                .show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.parseColor("#00BCD4"))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.parseColor("#00BCD4"))
    }

    enum class Type {
        Double,
        Integer,
        DateTime,
        HoursMinutes
    }
}