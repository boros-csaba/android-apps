package com.boroscsaba.commonlibrary.viewelements

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.boroscsaba.commonlibrary.R

class InformationPopup(private val context: AppCompatActivity?) {

    fun show(title: String, description: String) {
        if (context == null) {
            return
        }

        AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(description)
                .setPositiveButton(R.string.ok) { _, _ -> }
                .show()
    }

}