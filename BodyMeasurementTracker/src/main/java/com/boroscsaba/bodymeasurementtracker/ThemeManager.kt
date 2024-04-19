package com.boroscsaba.bodymeasurementtracker

class ThemeManager: com.boroscsaba.commonlibrary.ThemeManager() {

    init {
        addColor(FOREGROUND_COLOR, "#D32F2F", "#000000")
        addGradient(TAB_LAYOUT_BACKGROUND_GRADIENT, "#D32F2F", "#F44336", "#D32F2F", "#F44336")
        addColor(TAB_LAYOUT_ICON_TINT, "#ffffff", "#ffffff")
        addColor(BACKGROUND_COLOR, "#dddddd", "#333333", "SEPARATOR_LINE")
    }
}