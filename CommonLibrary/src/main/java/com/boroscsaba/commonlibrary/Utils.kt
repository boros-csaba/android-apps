package com.boroscsaba.commonlibrary

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import java.lang.Exception
import java.text.DecimalFormat
import java.util.*
import android.util.DisplayMetrics
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.boroscsaba.commonlibrary.settings.SettingsHelper
import com.bumptech.glide.Glide
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs


class Utils {
    companion object {

        //region Number helpers
        fun toDoubleOrNull(text: String?): Double? {
            if (text == null) return null
            var value: Double? = null
            try {
                val replacedText = text.replace(',', '.')
                value = DecimalFormat.getInstance(Locale.US).parse(replacedText).toDouble()
            }
            catch(e: Exception) { }
            return value
        }

        fun round(value: Double, decimalPlaces: Int): Double {
            return try {
                var bigDecimal = BigDecimal(java.lang.Double.toString(value))
                bigDecimal = bigDecimal.setScale(decimalPlaces, RoundingMode.HALF_UP)
                bigDecimal.toDouble()
            } catch(e: Exception) {
                0.0
            }
        }

        //endregion

        //region Date helpers
        fun getDayNrFromDate(date: Long): Long {
            return date / (1000 * 60 * 60 * 24)
        }

        fun getDayOfMonth(date: Long): Int {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = date
            return calendar.get(Calendar.DAY_OF_MONTH)
        }

        fun getMonthName(context: Context, date: Long): String {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = date
            return getMonthName(context, calendar.get(Calendar.MONTH))
        }

        fun getMonthName(context: Context, month: Int): String {
            val months = arrayOf(
                    context.getString(R.string.january),
                    context.getString(R.string.february),
                    context.getString(R.string.march),
                    context.getString(R.string.april),
                    context.getString(R.string.may),
                    context.getString(R.string.june),
                    context.getString(R.string.july),
                    context.getString(R.string.august),
                    context.getString(R.string.september),
                    context.getString(R.string.october),
                    context.getString(R.string.november),
                    context.getString(R.string.december))
            return months[month]
        }

        fun areOnSameDay(date1: Long, date2: Long): Boolean {
            return getDayNrFromDate(date1) == getDayNrFromDate(date2)
        }

        fun getDateAt(date: Long, hours: Int, minutes: Int): Long {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = date
            calendar.set(Calendar.HOUR_OF_DAY, hours)
            calendar.set(Calendar.MINUTE, minutes)
            return calendar.timeInMillis
        }

        fun getNoonAtDate(date: Long): Long {
            return getDateAt(date, 12, 0)
        }

        fun addYearsToDate(date: Long, years: Int): Long {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = date
            calendar.add(Calendar.YEAR, years)
            return calendar.timeInMillis
        }

        fun getDateAt(year: Int, month: Int, day: Int): Long {
            val newDate = Calendar.getInstance()
            newDate.set(year, month, day)
            return newDate.timeInMillis
        }

        fun getFormattedDateString(value: Long, context: Context): String {
            return SettingsHelper(context.applicationContext as ApplicationBase).getDateFormat().format(value)
        }

        fun getNrOfDaysPassed(date: Long): Int {
            return getNrOfDaysPassed(date, System.currentTimeMillis())
        }

        fun getNrOfDaysPassed(date1: Long, date2: Long): Int {
            return abs(getDayNrFromDate(date1) - getDayNrFromDate(date2)).toInt()
        }
        //endregion

        fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
            val matrix = Matrix().apply { postRotate(degrees) }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        fun convertDpToPixel(dp: Float, context: Context): Float {
            return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }

        fun convertPixelsToDp(px: Float, context: Context): Float {
            return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }

        fun getBitmapFromVectorDrawable(resourceId: Int, context: Context): Bitmap? {
            val drawable = ContextCompat.getDrawable(context, resourceId) ?: return null
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }

        fun setImageViewSource(resourceId: Int, imageView: ImageView?, context: Context) {
            if (imageView == null) return
            try {
                Glide.with(imageView).load(resourceId).into(imageView)
            }
            catch (e: Exception) {
                LoggingHelper.logException(e, context)
            }
        }

        fun getDistanceBetween(x1: Float, y1: Float, x2: Float, y2: Float): Float {
            return Math.sqrt(Math.pow((x2 - x1).toDouble(), 2.0) + Math.pow((y2 - y1).toDouble(), 2.0)).toFloat()
        }

        fun openActivity(context: Context, activityClass: Class<*>, pageIndex: Int? = null) {
            val intent = Intent(context, activityClass)
            if (pageIndex != null) {
                intent.putExtra("pageIndex", pageIndex)
            }
            context.startActivity(intent)
        }

        fun <T> performUncheckableCase(input: Any): T {
            @Suppress("UNCHECKED_CAST")
            return input as T
        }
    }
}