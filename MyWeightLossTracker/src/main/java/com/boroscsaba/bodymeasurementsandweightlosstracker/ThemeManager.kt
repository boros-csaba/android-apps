package com.boroscsaba.myweightlosstracker

class ThemeManager: com.boroscsaba.commonlibrary.ThemeManager() {

    init {
        addColor(BACKGROUND_COLOR, "#eeeeee", "#000000")
        addColor(FOREGROUND_COLOR, "#44A868", "#00783d")
        addColor(IMAGE_TINT, "#666666", "#666666", "ADD_MEASUREMENT")
        addColor(BACKGROUND_COLOR, "#ffffff", "#222222", "PARAMETER")
    }
}