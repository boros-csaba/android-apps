package com.boroscsaba.commonlibrary.viewelements

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.boroscsaba.commonlibrary.R
import kotlinx.android.synthetic.main.simple_popup_options_layout.view.*

class SimplePopupWithOptions(context: Context) : AlertDialog(context) {

    @SuppressLint("InflateParams")
    private val view = LayoutInflater.from(context).inflate(R.layout.simple_popup_options_layout, null)

    init {
        setView(view)
    }

    fun setOption1(textResourceId: Int, action: () -> Unit) {
        view.option1.visibility = View.VISIBLE
        view.option1.text = context.getString(textResourceId)
        view.option1.setOnClickListener {
            action.invoke()
            dismiss()
        }
    }

    fun setOption2(textResourceId: Int, action: () -> Unit) {
        view.option2.visibility = View.VISIBLE
        view.option2.text = context.getString(textResourceId)
        view.option2.setOnClickListener {
            action.invoke()
            dismiss()
        }
    }

    fun setOption3(textResourceId: Int, action: () -> Unit) {
        view.option3.visibility = View.VISIBLE
        view.option3.text = context.getString(textResourceId)
        view.option3.setOnClickListener {
            action.invoke()
            dismiss()
        }
    }
}