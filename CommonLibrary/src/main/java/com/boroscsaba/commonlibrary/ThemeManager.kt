package com.boroscsaba.commonlibrary

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Color

abstract class ThemeManager {

    var isDarkThemeByDefault = false
    private val colors = HashMap<String, Int>()
    private val darkColors = HashMap<String, Int>()

    init {
        addColor(BACKGROUND_COLOR, "#ffffff","#000000")
        addColor(BORDER_COLOR, "#999999", "#444444")
        addColor(PRIMARY_TEXT_COLOR, "#333333", "#ffffff")
        addColor(PRIMARY_TEXT_COLOR, "#777777", "#aaaaaa", "LIGHT")
        addColor(SECONDARY_TEXT_COLOR, "#666666", "#aaaaaa")
        addColor(SHADOW_COLOR, "#55000000", "#55ffffff")
        addColor(DRAWER_BACKGROUND_COLOR, "#ffffff", "#373737")
        addColor(DRAWER_TEXT_COLOR, "#666666", "#ffffff")
        addColor(TUTORIAL_ARROW_COLOR, "#333333", "#ffffff")
        addColor(TAB_LAYOUT_ICON_TINT, "#333333", "#ffffff")

        addColor(CALENDAR_DAYS_LABEL_COLOR, "#666666", "#dddddd")
        addColor(CALENDAR_DAYS_HIGHLIGHTED_LABEL_COLOR, "#333333", "#ffffff")
        addColor(CALENDAR_DAYS_HIGHLIGHTED_BACKGROUND_COLOR, "#cfff95", "#6b9b37")

        addColor(BAR_CHART_LABELS_COLOR, "#333333", "#dddddd")

        addColor(INACTIVE_BACKGROUND_BACKGROUND_COLOR, "#eeeeee", "#ff0000")

        setupPopupEditorStyle()
    }
    
    fun addColor(type: String, color: String, colorDark: String, styleName: String? = null) {
        if (styleName == null) {
            colors["default$type"] = Color.parseColor(color)
            darkColors["default$type"] = Color.parseColor(colorDark)
        }
        else {
            colors[styleName + type] = Color.parseColor(color)
            darkColors[styleName + type] = Color.parseColor(colorDark)
        }
    }

    fun getColor(type: String, styleName: String? = null): Int {
        return if (styleName == null || !colors.containsKey(styleName + type)) {
            if (isDarkMode) darkColors["default$type"]!!
            else colors["default$type"]!!
        }
        else {
            if (isDarkMode) darkColors[styleName + type]!!
            else colors[styleName + type]!!
        }
    }

    fun hasColor(type: String, styleName: String? = null): Boolean {
        return if (styleName == null) {
            colors.containsKey("default$type")
        }
        else {
            colors.containsKey(styleName + type)
        }
    }

    fun addGradient(type: String, startColor: String, endColor: String, startColorDark: String, endColorDark: String, styleName: String? = null) {
        if (styleName == null) {
            colors["default$type$GRADIENT_START"] = Color.parseColor(startColor)
            colors["default$type$GRADIENT_END"] = Color.parseColor(endColor)
            darkColors["default$type$GRADIENT_START"] = Color.parseColor(startColorDark)
            darkColors["default$type$GRADIENT_END"] = Color.parseColor(endColorDark)
        }
        else {
            colors[styleName + type + GRADIENT_START] = Color.parseColor(startColor)
            colors[styleName + type + GRADIENT_END] = Color.parseColor(endColor)
            darkColors[styleName + type + GRADIENT_START] = Color.parseColor(startColorDark)
            darkColors[styleName + type + GRADIENT_END] = Color.parseColor(endColorDark)
        }
    }

    fun hasGradient(type: String, styleName: String? = null): Boolean {
        if (styleName == null) {
            return colors.containsKey("default$type$GRADIENT_START") && colors.containsKey("default$type$GRADIENT_END")
        }
        else {
            if (colors.containsKey(styleName + type + GRADIENT_START) && colors.containsKey(styleName + type + GRADIENT_END)) return true
            if (!colors.containsKey(styleName + type) && colors.containsKey("default$type$GRADIENT_START") && colors.containsKey("default$type$GRADIENT_END")) return true
            return false
        }
    }

    fun getGradientStartColor(type: String, styleName: String? = null): Int {
        return if (styleName == null || !colors.containsKey(styleName + type + GRADIENT_START)) {
            if (isDarkMode && darkColors.contains("default$type$GRADIENT_START")) darkColors["default$type$GRADIENT_START"]!!
            else colors["default$type$GRADIENT_START"]!!
        }
        else {
            if (isDarkMode && darkColors.contains(styleName + type + GRADIENT_START)) darkColors[styleName + type + GRADIENT_START]!!
            else colors[styleName + type + GRADIENT_START]!!
        }
    }

    fun getGradientEndColor(type: String, styleName: String? = null): Int {
        return if (styleName == null || !colors.containsKey(styleName + type + GRADIENT_END)) {
            if (isDarkMode && darkColors.contains("default$type$GRADIENT_END")) darkColors["default$type$GRADIENT_END"]!!
            else colors["default$type$GRADIENT_END"]!!
        }
        else {
            if (isDarkMode && darkColors.contains(styleName + type + GRADIENT_END)) darkColors[styleName + type + GRADIENT_END]!!
            else colors[styleName + type + GRADIENT_END]!!
        }
    }



    fun saveSetting(isDarkMode: Boolean, context: Context) {
        val sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preferences_name), MODE_PRIVATE)
        with (sharedPreferences.edit()) {
            putBoolean("isDarkMode", isDarkMode)
            apply()
        }
    }

    fun initSetting(context: Context) {
        val sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preferences_name), MODE_PRIVATE)
        isDarkMode = sharedPreferences.getBoolean("isDarkMode", isDarkThemeByDefault)
    }

    companion object {
        var isDarkMode = false
        private const val GRADIENT_START = "gradient_start"
        private const val GRADIENT_END = "gradient_end"

        const val FOREGROUND_COLOR = "_foreground_color"
        const val BACKGROUND_COLOR = "_background_color"
        const val BORDER_COLOR = "_border_color"
        const val SHADOW_COLOR = "_shadow_color"

        const val TOOLBAR_BACKGROUND_COLOR = "_toolbar_background_color"
        const val TAB_LAYOUT_BACKGROUND_COLOR = "_tab_layout_background_color"

        const val BACKGROUND_GRADIENT = "_background_gradient"
        const val TAB_LAYOUT_BACKGROUND_GRADIENT = "_tab_layout_background_gradient"

        const val PRIMARY_TEXT_COLOR = "_primary_text_color"
        const val SECONDARY_TEXT_COLOR = "_secondary_text_color"
        const val DRAWER_BACKGROUND_COLOR = "_drawer_background_color"
        const val DRAWER_TEXT_COLOR = "_drawer_text_color"
        const val TUTORIAL_ARROW_COLOR = "_tutorial_arrow_color"
        const val TAB_LAYOUT_ICON_TINT = "_tab_layout_icon_tint"
        const val IMAGE_TINT = "_image_tint"

        const val CALENDAR_DAYS_LABEL_COLOR = "CALENDAR_DAYS_LABEL_COLOR"
        const val CALENDAR_DAYS_HIGHLIGHTED_LABEL_COLOR = "CALENDAR_DAYS_HIGHLIGHTED_LABEL_COLOR"
        const val CALENDAR_DAYS_HIGHLIGHTED_BACKGROUND_COLOR = "CALENDAR_DAYS_HIGHLIGHTED_BACKGROUND_COLOR"

        const val BAR_CHART_LABELS_COLOR = "BAR_CHART_LABELS_COLOR"

        const val LINE_CHART_LINE_COLOR = "LINE_CHART_LINE_COLOR"

        const val ACTIVE_BACKGROUND_BACKGROUND_COLOR = "ACTIVE_BACKGROUND_BACKGROUND_COLOR"
        const val INACTIVE_BACKGROUND_BACKGROUND_COLOR = "INACTIVE_BACKGROUND_BACKGROUND_COLOR"

        private const val BACKGROUND_GRADIENT_START = "_background_gradient_start"
        private const val BACKGROUND_GRADIENT_END = "_background_gradient_end"

        const val STYLE_POPUP_EDITOR = "STYLE_POPUP_EDITOR"
    }

    private fun setupPopupEditorStyle() {
        addColor(PRIMARY_TEXT_COLOR, "#555555", "#ffffff", STYLE_POPUP_EDITOR)
        addColor(BACKGROUND_COLOR, "#ffffff", "#333333", STYLE_POPUP_EDITOR)
        addColor(IMAGE_TINT, "#555555", "#ffffff", STYLE_POPUP_EDITOR)
    }
}