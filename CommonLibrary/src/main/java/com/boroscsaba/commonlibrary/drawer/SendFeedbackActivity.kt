package com.boroscsaba.commonlibrary.drawer

import android.content.Intent
import android.os.Bundle
import com.google.android.material.textfield.TextInputLayout
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.commonlibrary.helpers.PremiumHelper
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.activities.ActivityBase

class SendFeedbackActivity : ActivityBase() {

    init {
        options.layout = R.layout.activity_send_feedback
        options.toolbarId = R.id.activity_send_feedback_toolbar
        options.canShowAdConsentPopup = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initialText = intent.getStringExtra("InitialText") ?: ""
        findViewById<TextInputLayout>(R.id.activity_send_feedback_text_layout).editText?.setText(initialText)
    }

    override fun setListeners() {}

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.send_feedback_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.send_feedback_action_send -> {
                sendFeedback()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun sendFeedback() {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "message/rfc822"
        i.putExtra(Intent.EXTRA_EMAIL, arrayOf("boros.csaba94@gmail.com"))
        i.putExtra(Intent.EXTRA_SUBJECT, "${getString(R.string.app_name)} feedback")

        val textInputLayout = findViewById<TextInputLayout>(R.id.activity_send_feedback_text_layout)
        val editText = textInputLayout.editText
        var message: String
        message = editText?.text?.toString() ?: ""

        if (message.isEmpty()) {
            textInputLayout.error = getString(R.string.too_short)
            return
        }

        var deviceId = LoggingHelper().getDeviceId(this)
        deviceId += if (PremiumHelper.isPremium) {
            "P_${PremiumHelper.orderId}"
        } else {
            "N"
        }
        message = "Id: $deviceId\nVersion: ${(application as ApplicationBase).appVersion}\n\n$message"
        i.putExtra(Intent.EXTRA_TEXT, message)

        try {
            startActivity(Intent.createChooser(i, "Send mail..."))
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show()
        }

    }
}
