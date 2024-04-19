package com.boroscsaba.commonlibrary.helpers

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.drawer.SendFeedbackActivity
import com.boroscsaba.commonlibrary.viewelements.Popup
import android.widget.FrameLayout
import com.google.android.material.snackbar.Snackbar
import android.app.Activity
import android.graphics.Color
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.commonlibrary.Utils


/**
 * Created by Boros Csaba
 */

object AppRatingHelper {

    fun showRateAppPopup(context: Context) {
        LoggingHelper.logEvent(context, "rate_app_popup_show")
        val popup = Popup(R.layout.rate_app_popup, context)
        popup.title = context.getString(R.string.rate_us)
        popup.show { view ->
            view.findViewById<ImageView>(R.id.icon).setImageResource((context.applicationContext as ApplicationBase).splashScreenIcon)
            view.findViewById<ImageView>(R.id.star5).setOnClickListener {
                LoggingHelper.logEvent(context, "rate_app_popup_5_stars")
                goToPlayStore(context)
                popup.popup?.dismiss()
            }
            view.findViewById<ImageView>(R.id.star4).setOnClickListener { goToFeedbackPage(4, "popup", context); popup.popup?.dismiss() }
            view.findViewById<ImageView>(R.id.star3).setOnClickListener { goToFeedbackPage(3, "popup", context); popup.popup?.dismiss() }
            view.findViewById<ImageView>(R.id.star2).setOnClickListener { goToFeedbackPage(2, "popup", context); popup.popup?.dismiss() }
            view.findViewById<ImageView>(R.id.star1).setOnClickListener { goToFeedbackPage(1, "popup", context); popup.popup?.dismiss() }
        }
    }

    @SuppressLint("InflateParams")
    fun showRateAppSnackBar(activity: Activity) {
        LoggingHelper.logEvent(activity, "rate_app_snack_bar_show")
        val snackBar = Snackbar.make(activity.window.decorView.findViewById(android.R.id.content), "", Snackbar.LENGTH_INDEFINITE)
        snackBar.duration = 10000
        val height = Utils.convertDpToPixel(130f, activity)
        val snackView = activity.layoutInflater.inflate(R.layout.rate_app_snack_bar_layout, null)
        snackBar.view.setBackgroundColor(Color.TRANSPARENT)
        val snackBarView = snackBar.view as Snackbar.SnackbarLayout
        val parentParams = snackBarView.layoutParams as FrameLayout.LayoutParams
        parentParams.height = height.toInt()
        parentParams.width = FrameLayout.LayoutParams.MATCH_PARENT
        snackBarView.layoutParams = parentParams
        snackBarView.addView(snackView, 0)
        snackBar.show()
        snackBar.view.findViewById<ImageView>(R.id.star5).setOnClickListener {
            LoggingHelper.logEvent(activity, "rate_app_snack_bar_5_stars")
            goToPlayStore(activity)
            snackBar.dismiss()
        }
        snackBar.view.findViewById<ImageView>(R.id.star4).setOnClickListener { goToFeedbackPage(4, "snack_bar", activity); snackBar.dismiss() }
        snackBar.view.findViewById<ImageView>(R.id.star3).setOnClickListener { goToFeedbackPage(3, "snack_bar", activity); snackBar.dismiss() }
        snackBar.view.findViewById<ImageView>(R.id.star2).setOnClickListener { goToFeedbackPage(2, "snack_bar", activity); snackBar.dismiss() }
        snackBar.view.findViewById<ImageView>(R.id.star1).setOnClickListener { goToFeedbackPage(1, "snack_bar", activity); snackBar.dismiss() }
    }

    private fun goToPlayStore(context: Context) {
        val uri = Uri.parse("market://details?id=" + context.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            context.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)))
        }
    }

    private fun goToFeedbackPage(stars: Int, source: String, context: Context) {
        LoggingHelper.logEvent(context, "rate_app_feedback_$source", "stars", stars.toString())
        val intent = Intent(context, SendFeedbackActivity::class.java)
        intent.putExtra("InitialText", "${context.getString(R.string.my_rating)}: $stars/5 ${context.getString(R.string.stars)}\n${context.getString(R.string.rate_app_comment)}")
        context.startActivity(intent)
    }
}
