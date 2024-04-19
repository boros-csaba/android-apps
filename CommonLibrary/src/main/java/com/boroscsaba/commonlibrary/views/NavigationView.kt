package com.boroscsaba.commonlibrary.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.boroscsaba.commonlibrary.*
import com.boroscsaba.commonlibrary.activities.ActivityBase
import com.boroscsaba.commonlibrary.drawer.DrawerButton
import com.boroscsaba.commonlibrary.helpers.PremiumHelper

class NavigationView: com.google.android.material.navigation.NavigationView {

    constructor(context: Context): super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        inflate(context, R.layout.navigation_drawer_layout, this)
        val app = (context.applicationContext as ApplicationBase)
        val textColor = app.themeManager.getColor(ThemeManager.DRAWER_TEXT_COLOR)

        setBackgroundColor(app.themeManager.getColor(ThemeManager.DRAWER_BACKGROUND_COLOR))
        findViewById<TextView>(R.id.appTitle).setTextColor(textColor)
        val appVersionTextView = findViewById<TextView>(R.id.appVersion)
        appVersionTextView.setTextColor(textColor)
        val versionText = app.appVersion + if (PremiumHelper.isPremium) {
            " - " + app.getString(R.string.non_translated_premium_version)
        } else {
            " - " + app.getString(R.string.non_translated_standard_version)
        }
        appVersionTextView.text = versionText
    }

    fun setIcon(iconResourceId: Int) {
        val icon = findViewById<ImageView>(R.id.appIcon)
        Utils.setImageViewSource(iconResourceId, icon, context)
    }

    fun addButtons(buttonsList: ArrayList<DrawerButton>, activity: ActivityBase, drawer: DrawerLayout) {
        var buttons = buttonsList
        val app = (context.applicationContext as ApplicationBase)
        val textColor = app.themeManager.getColor(ThemeManager.DRAWER_TEXT_COLOR)
        val constraintLayout = findViewById<ConstraintLayout>(R.id.parent)

        if (constraintLayout.childCount > 3) {
            LoggingHelper.logException(Exception("Possible3 Duplicate: ${app.appStartLog}"), context)
            return
        }

        val settingsButtonCount = buttons.count { b -> b.titleResourceId == R.string.navigation_drawer_settings }
        if (settingsButtonCount != 1) {
            app.appStartLog += " BTNS: "
            for (btn in buttons) {
                app.appStartLog += context.getString(btn.titleResourceId) + ", "
            }
            LoggingHelper.logException(Exception("PossibleBtn Duplicate: ${app.appStartLog}"), context)
            buttons = ArrayList(buttons.distinctBy { b -> b.titleResourceId })
        }
        else {
            for (i in 0 until constraintLayout.childCount) {
                if (constraintLayout.getChildAt(i) is TextView && (constraintLayout.getChildAt(i) as TextView).text.toString()  == context.getString(R.string.navigation_drawer_settings)) {
                    LoggingHelper.logException(Exception("Possible4 Duplicate: ${app.appStartLog}"), context)
                }
            }
        }

        buttons.sortBy { b -> b.order }
        buttons.sortBy { b -> b.group }

        var previousIcon: ImageView? = null
        var previousGroup = 0
        val iconSize = Utils.convertDpToPixel(48f, context).toInt()
        val iconPadding = Utils.convertDpToPixel(11f, context).toInt()
        buttons.forEach { button ->
            if (button.titleResourceId != R.string.navigation_drawer_premium || !PremiumHelper.isPremium) {
                val icon = ImageView(context)
                icon.id = View.generateViewId()
                constraintLayout.addView(icon)
                val buttonTextView = TextView(context)
                buttonTextView.id = View.generateViewId()
                constraintLayout.addView(buttonTextView)
                val darkModeSwitch = if (button.titleResourceId == R.string.dark_mode) {
                    val switch = Switch(context)
                    switch.id = R.id.dark_mode_switch
                    constraintLayout.addView(switch)
                    switch.setPadding(iconPadding/2, iconPadding, iconPadding/2, iconPadding)
                    switch
                }
                else {
                    null
                }

                icon.imageTintList = ColorStateList.valueOf(textColor)
                Utils.setImageViewSource(button.iconResourceId, icon, context)
                icon.layoutParams = ConstraintLayout.LayoutParams(iconSize, iconSize)
                icon.setPadding(iconPadding, iconPadding, iconPadding, iconPadding)
                buttonTextView.setPadding(0, iconPadding, 0, iconPadding)

                val constraintSet = ConstraintSet()
                constraintSet.clone(constraintLayout)
                if (previousIcon == null) {
                    constraintSet.connect(icon.id, ConstraintSet.TOP, R.id.appVersion, ConstraintSet.BOTTOM, Utils.convertDpToPixel(48f, context).toInt())
                }
                else {
                    val marginDp = if (previousGroup == button.group) {
                        4f
                    }
                    else {
                        20f
                    }
                    constraintSet.connect(icon.id, ConstraintSet.TOP, previousIcon!!.id, ConstraintSet.BOTTOM, Utils.convertDpToPixel(marginDp, context).toInt())
                }
                constraintSet.connect(icon.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, Utils.convertDpToPixel(16f, context).toInt())
                constraintSet.connect(icon.id, ConstraintSet.END, buttonTextView.id, ConstraintSet.START)
                previousIcon = icon
                previousGroup = button.group

                buttonTextView.setText(button.titleResourceId)
                buttonTextView.setTextColor(textColor)
                buttonTextView.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)

                constraintSet.connect(buttonTextView.id, ConstraintSet.TOP, icon.id, ConstraintSet.TOP)
                constraintSet.connect(buttonTextView.id, ConstraintSet.BOTTOM, icon.id, ConstraintSet.BOTTOM)
                constraintSet.connect(buttonTextView.id, ConstraintSet.START, icon.id, ConstraintSet.END, Utils.convertDpToPixel(24f, context).toInt())

                if (darkModeSwitch != null) {
                    constraintSet.connect(darkModeSwitch.id, ConstraintSet.TOP, icon.id, ConstraintSet.TOP)
                    constraintSet.connect(darkModeSwitch.id, ConstraintSet.BOTTOM, icon.id, ConstraintSet.BOTTOM)
                    constraintSet.connect(darkModeSwitch.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, Utils.convertDpToPixel(16f, context).toInt())

                    darkModeSwitch.isChecked = ThemeManager.isDarkMode
                    darkModeSwitch.setOnCheckedChangeListener { buttonView, _ ->
                        button.action.invoke(activity)
                        drawer.closeDrawer(GravityCompat.START)
                        buttonView.isChecked = ThemeManager.isDarkMode
                    }
                }

                icon.setOnClickListener {
                    button.action.invoke(activity)
                    if (darkModeSwitch == null) {
                        drawer.closeDrawer(GravityCompat.START)
                    }
                    else {
                        darkModeSwitch.isChecked = ThemeManager.isDarkMode
                    }
                }
                buttonTextView.setOnClickListener {
                    button.action.invoke(activity)
                    if (darkModeSwitch == null) {
                        drawer.closeDrawer(GravityCompat.START)
                    }
                    else {
                        darkModeSwitch.isChecked = ThemeManager.isDarkMode
                    }
                }

                constraintSet.applyTo(constraintLayout)
            }
        }
    }
}