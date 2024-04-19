package com.boroscsaba.commonlibrary.helpers

import android.view.View
import android.view.ViewGroup
import com.boroscsaba.commonlibrary.R

class ViewHelper {
    companion object {

        fun findViewByTagName(container: ViewGroup, name: String): View? {
            for (i in 0 until container.childCount) {
                val view = container.getChildAt(i)
                if (view.getTag(R.id.tag_name) == name) {
                    return view
                }
                if (view is ViewGroup) {
                    for (j in 0 until view.childCount) {
                        val innerView = view.getChildAt(j)
                        if (innerView.getTag(R.id.tag_name) == name) {
                            return innerView
                        }
                    }
                }
            }
            return null
        }
    }
}