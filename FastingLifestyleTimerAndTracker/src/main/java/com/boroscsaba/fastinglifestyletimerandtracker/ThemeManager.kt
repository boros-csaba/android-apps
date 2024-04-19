package com.boroscsaba.fastinglifestyletimerandtracker

class ThemeManager: com.boroscsaba.commonlibrary.ThemeManager() {

    init {
        addColor(FOREGROUND_COLOR, "#ED6F60", "#000000")
        addColor(TAB_LAYOUT_ICON_TINT, "#ffffff", "#ffffff")
        addColor(BACKGROUND_COLOR, "#ED6F60", "#000000", "TIMER_PAGE")
    }
}