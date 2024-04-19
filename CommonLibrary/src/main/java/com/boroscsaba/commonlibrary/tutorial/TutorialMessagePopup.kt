package com.boroscsaba.commonlibrary.tutorial

import android.app.Activity
import android.widget.TextView
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.viewelements.Popup

class TutorialMessagePopup(private val titleResourceId: Int, private val textResourceId: Int) {

    var action: (() -> Unit)? = null

    fun show(context: Activity) {
        val popup = Popup(R.layout.tutorial_message_layout, context)
        popup.showTitleBar = false
        popup.show { view ->
            view.findViewById<TextView>(R.id.title).setText(titleResourceId)
            view.findViewById<TextView>(R.id.message).setText(textResourceId)
            view.findViewById<TextView>(R.id.okButton).setOnClickListener {
                popup.popup?.dismiss()
                action?.invoke()
            }
        }
    }
}