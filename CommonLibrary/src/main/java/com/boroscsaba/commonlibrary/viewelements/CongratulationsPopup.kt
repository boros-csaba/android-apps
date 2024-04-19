package com.boroscsaba.commonlibrary.viewelements

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.boroscsaba.commonlibrary.R

class CongratulationsPopup(private val context: AppCompatActivity?) {

    fun show(): AlertDialog? {
        if (context == null) { return null }
        val dialog = AlertDialog.Builder(context, R.style.FullScreenDialogStyle)
                .setView(R.layout.congratulations_popup_layout)
                .show()
        dialog.findViewById<ConstraintLayout>(R.id.congratulationsPopupContainer)?.setOnClickListener { dialog.cancel() }
        return dialog
    }

}