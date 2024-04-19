package com.boroscsaba.commonlibrary.viewelements

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import java.util.*
import android.view.LayoutInflater
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.ThemeManager
import com.boroscsaba.commonlibrary.Utils


class CustomCalendar : LinearLayout {

    private val calendarEntries = ArrayList<CalendarEntry>()
    private var emptyDateOnClickListener: ((date: Long) -> Unit)? = null
    private var textColor = Color.parseColor("#333333")
    private var highlightedTextColor = textColor
    private var highlightedBackgroundColor = textColor

    constructor(context: Context): super(context) { init(null) }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) { init(attrs) }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) { init(attrs) }

    private fun init(attrs: AttributeSet?) {
        inflate(context, R.layout.custom_calendar_layout,this)
        orientation = VERTICAL
        setPagerAdapter()

        val themeManager = (context.applicationContext as ApplicationBase).themeManager
        var styleName: String? = null

        if (attrs != null) {
            val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.CustomCalendar)
            styleName = styledAttributes.getString(R.styleable.CustomCalendar_styleName)
            styledAttributes.recycle()
        }
        textColor = themeManager.getColor(ThemeManager.CALENDAR_DAYS_LABEL_COLOR, styleName)
        highlightedTextColor = themeManager.getColor(ThemeManager.CALENDAR_DAYS_HIGHLIGHTED_LABEL_COLOR, styleName)
        highlightedBackgroundColor = themeManager.getColor(ThemeManager.CALENDAR_DAYS_HIGHLIGHTED_BACKGROUND_COLOR, styleName)
    }

    fun setEmptyDateOnClickListener(onClickListener: (date: Long) -> Unit) {
        this.emptyDateOnClickListener = onClickListener
    }

    fun setCalendarEntries(entries: ArrayList<CalendarEntry>) {
        calendarEntries.clear()
        calendarEntries.addAll(entries)
        setPagerAdapter()
    }

    private fun setPagerAdapter() {
        val viewPager = findViewById<ViewPager>(R.id.calendarPager)
        viewPager.adapter = CalendarViewPagerAdapter(context, this, emptyDateOnClickListener, textColor, highlightedTextColor, highlightedBackgroundColor)
        viewPager.currentItem = 120
    }

    fun navigateLeft() {
        val viewPager = findViewById<ViewPager>(R.id.calendarPager)
        viewPager.currentItem--
    }

    fun navigateRight() {
        val viewPager = findViewById<ViewPager>(R.id.calendarPager)
        viewPager.currentItem++
    }

    class CalendarViewPagerAdapter(private val context: Context, private val parent: CustomCalendar, private val emptyDateOnClickListener: ((date: Long) -> Unit)?, private val textColor: Int, private val highlightedLabelColor: Int, private val highlightedBackgroundColor: Int): PagerAdapter() {

        override fun getCount(): Int {
            return 240
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater.inflate(R.layout.custom_calendar_month_layout, container, false)

            val leftArrow = view.findViewById<TextView>(R.id.leftArrow)
            leftArrow.setOnClickListener { parent.navigateLeft() }
            val rightArrow = view.findViewById<TextView>(R.id.rightArrow)
            rightArrow.setOnClickListener { parent.navigateRight() }

            val row1 = view.findViewById<LinearLayout>(R.id.row1)
            val row2 = view.findViewById<LinearLayout>(R.id.row2)
            val row3 = view.findViewById<LinearLayout>(R.id.row3)
            val row4 = view.findViewById<LinearLayout>(R.id.row4)
            val row5 = view.findViewById<LinearLayout>(R.id.row5)
            val row6 = view.findViewById<LinearLayout>(R.id.row6)

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, position - 120)
            calendar.set(Calendar.DAY_OF_MONTH, 1)

            val monthLabel = view.findViewById<TextView>(R.id.monthLabel)
            monthLabel.text = String.format("%s %d", Utils.getMonthName(context, calendar.get(Calendar.MONTH)), calendar.get(Calendar.YEAR))

            var firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK) - 1
            if (firstDayOfMonth == 0) firstDayOfMonth = 7
            val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            calendar.set(Calendar.DAY_OF_MONTH, daysInMonth)
            var lastDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK) - 1
            if (lastDayOfMonth == 0) lastDayOfMonth = 7

            for (emptyDays in 1 until firstDayOfMonth) {
                createEmptySpace(row1)
                createCalendarDateView(row1, "")
            }

            var day = 0
            var row = row1
            for (dayColumn in firstDayOfMonth until daysInMonth + firstDayOfMonth) {
                day++
                calendar.set(Calendar.DAY_OF_MONTH, day)
                row = when {
                    dayColumn <= 7 -> row1
                    dayColumn <= 14 -> row2
                    dayColumn <= 21 -> row3
                    dayColumn <= 28 -> row4
                    dayColumn <= 35 -> row5
                    else -> row6
                }
                createEmptySpace(row)
                createCalendarDateView(row, day.toString(), calendar.timeInMillis)
                if (dayColumn == 7 || dayColumn == 14 || dayColumn == 21 || dayColumn == 28 || dayColumn == 35) {
                    createEmptySpace(row)
                }
            }
            for (emptyDays in lastDayOfMonth until 7) {
                createEmptySpace(row)
                createCalendarDateView(row, "")
            }
            if (lastDayOfMonth < 7) {
                createEmptySpace(row)
            }

            container.addView(view)
            return view
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view == obj
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }

        private fun createCalendarDateView(container: LinearLayout, text: String, date: Long? = null) {
            inflate(context, R.layout.custom_calendar_date_layout, container)
            val textView = container.getChildAt(container.childCount - 1) as TextView
            textView.setTextColor(textColor)
            textView.text = text
            if (date != null) {
                val entry = parent.calendarEntries.firstOrNull { e -> Utils.areOnSameDay(e.date, date) }
                if (entry != null) {
                    textView.setBackgroundResource(R.drawable.circle)
                    textView.setTextColor(highlightedLabelColor)
                    textView.backgroundTintList = ColorStateList.valueOf(highlightedBackgroundColor)
                    textView.setOnClickListener { entry.action?.invoke() }
                }
                else {
                    textView.setOnClickListener { emptyDateOnClickListener?.invoke(date) }
                }
            }
        }

        private fun createEmptySpace(container: LinearLayout) {
            val space = Space(context)
            val param = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f)
            space.layoutParams = param
            container.addView(space)
        }
    }
}