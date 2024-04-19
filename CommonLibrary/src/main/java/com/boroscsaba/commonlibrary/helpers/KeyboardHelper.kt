package com.boroscsaba.commonlibrary.helpers

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Created by Boros Csaba
 */
class KeyboardHelper(val context: Activity) {

    fun hideKeyboard() {
        val view = context.currentFocus
        if (view != null) {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    fun showKeyboard(view: View) {
        view.requestFocus()
        val inputService = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputService?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}