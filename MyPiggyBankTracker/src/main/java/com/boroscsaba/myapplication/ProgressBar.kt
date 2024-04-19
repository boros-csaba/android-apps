package com.boroscsaba.myapplication

import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.progress_bar.*

/**
 * Created by Boros Csaba
 */

class ProgressBar(private val context: AppCompatActivity) {

    fun setPercentage(percentage: Int) {
        val layerDrawable = context.progressBarImage.drawable as LayerDrawable
        val clipDrawable = layerDrawable.findDrawableByLayerId(R.id.progressBar_primaryProgressBar) as ClipDrawable
        clipDrawable.level = percentage * 100

        val secondaryClipDrawable = layerDrawable.findDrawableByLayerId(R.id.progressBar_secondaryProgressBar) as ClipDrawable
        secondaryClipDrawable.level = 0

        setPlusSignVisibility(View.INVISIBLE)
    }

    fun setSecondaryPercentage(percentage: Int) {
        val layerDrawable = context.progressBarImage.drawable as LayerDrawable
        val clipDrawable = layerDrawable.findDrawableByLayerId(R.id.progressBar_primaryProgressBar) as ClipDrawable
        clipDrawable.level = 10000

        val secondaryClipDrawable = layerDrawable.findDrawableByLayerId(R.id.progressBar_secondaryProgressBar) as ClipDrawable
        secondaryClipDrawable.level = percentage * 100

        setPlusSignVisibility(View.VISIBLE)
    }

    fun setText(text: String) {
        context.progressBarText.text = text
    }

    private fun setPlusSignVisibility(visibility: Int) {
        context.progressBarPlusSign.visibility = visibility
    }

}
