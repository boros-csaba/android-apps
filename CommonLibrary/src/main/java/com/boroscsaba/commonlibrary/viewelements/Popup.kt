package com.boroscsaba.commonlibrary.viewelements

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.R
import kotlinx.android.synthetic.main.popup_layout.view.*

class Popup(private val layoutResourceId: Int, private val context: Context) {

    var title: String? = null
    var okAction: ((view: View) -> Unit)? = null
    var deleteAction: ((view: View) -> Unit)? = null
    var deleteAlertResourceId: Int? = null
    var closeAction: ((view: View) -> Unit)? = null
    var closeAlertResourceId: Int? = null
    var showDiscardButton = false
    var showTitleBar = true
    private var color: Int? = null
    var popup: AlertDialog? = null

    fun show(setupView: (view: View) -> Unit) {
        val containerView = inflateLayout(setupView)
        popup = AlertDialog.Builder(context)
                .setCancelable(false)
                .setView(containerView)
                .show()
        setupButtons(containerView, popup!!)
    }

    fun setColor(color: Int) {
        this.color = color
    }

    private fun inflateLayout(setupView: (view: View) -> Unit): View {
        val layoutInflater = LayoutInflater.from(context)
        val wrapperLayout = R.layout.popup_layout
        val containerLayout = layoutInflater.inflate(wrapperLayout, null)
        val containerView = containerLayout.findViewById<LinearLayout>(R.id.content)
        val view = layoutInflater.inflate(layoutResourceId, null)

        if (!showTitleBar) {
            containerLayout.titleContainer.visibility = View.GONE
        }

        if (color != null) {
            containerLayout.titleContainer.setBackgroundColor(color!!)
            containerLayout.ok.setBackgroundColor(color!!)
        }

        val title = title
        if (title != null) {
            val titleTextView = containerLayout.findViewById<TextView>(R.id.title)
            titleTextView.text = title
            if (color != null) {
                titleTextView.setBackgroundColor(color!!)
            }
        }

        setupView.invoke(view)
        containerView.addView(view)
        return containerLayout
    }

    private fun setupButtons(containerView: View, alertDialog: AlertDialog) {
        var hasButtons = false
        val closeButton = containerView.findViewById<TextView>(R.id.close)
        closeButton.setOnClickListener {
            if (closeAlertResourceId == null) {
                alertDialog.dismiss()
                closeAction?.invoke(containerView)
            }
            else {
                invokeWithAlert(closeAlertResourceId, alertDialog, containerView, R.string.discard) {
                    alertDialog.dismiss()
                    closeAction?.invoke(containerView)
                }
            }
        }

        val okButton = containerView.findViewById<ImageView>(R.id.ok)
        if (okAction == null) {
            okButton.visibility = View.GONE
        }
        else {
            hasButtons = true
            okButton.setOnClickListener {
                okAction?.invoke(containerView)
            }
        }

        val deleteButton = containerView.findViewById<ImageView>(R.id.delete)
        if (deleteAction == null) {
            deleteButton.visibility = View.GONE
        }
        else {
            hasButtons = true
            deleteButton.setOnClickListener {
                if (deleteAlertResourceId == null) {
                    alertDialog.dismiss()
                    deleteAction?.invoke(containerView)
                }
                else {
                    invokeWithAlert(deleteAlertResourceId, alertDialog, containerView, R.string.delete) {
                        alertDialog.dismiss()
                        deleteAction?.invoke(containerView)
                    }
                }
            }
        }

        val discardButton = containerView.findViewById<ImageView>(R.id.discard)
        if (!showDiscardButton) {
            discardButton.visibility = View.GONE
        }
        else {
            discardButton.setOnClickListener {
                closeAction?.invoke(it)
                alertDialog.dismiss()
            }
        }

        if (!hasButtons) {
            containerView.buttonsContainer.visibility = View.GONE
        }
    }

    private fun invokeWithAlert(alert: Int?, alertDialog: AlertDialog, containerView: View, positiveText: Int, action: ((view: View) -> Unit)?) {
        if (alert == null) return
        val builder = AlertDialog.Builder(context)
        builder.setMessage(context.getString(alert))
        builder.setCancelable(false)
        builder.setPositiveButton(context.getString(positiveText)) { dialog, _ ->
            alertDialog.dismiss()
            action?.invoke(containerView)
            dialog.cancel()
        }
        builder.setNegativeButton(context.getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
        val dialog = builder.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.parseColor("#00BCD4"))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.parseColor("#00BCD4"))
    }
}