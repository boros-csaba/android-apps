package com.boroscsaba.myapplication

class ThemeManager: com.boroscsaba.commonlibrary.ThemeManager() {

    init {
        addColor(FOREGROUND_COLOR, "#2fb1e2", "#002d53")
        addColor(PRIMARY_TEXT_COLOR, "#83b400", "#83b400", POSITIVE_AMOUNT_TEXT)
    }

    companion object {
        const val POSITIVE_AMOUNT_TEXT = "PositiveAmountText"
    }
}