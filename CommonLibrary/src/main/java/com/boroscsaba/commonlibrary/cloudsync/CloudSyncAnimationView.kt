package com.boroscsaba.commonlibrary.cloudsync

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.RotateDrawable
import android.util.AttributeSet
import android.view.View
import com.boroscsaba.commonlibrary.R

class CloudSyncAnimationView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var cloudDrawable: Drawable? = null
    private val syncDrawable = RotateDrawable()
    private val syncSizeRation = 0.40

    init {
        cloudDrawable = context.getDrawable(R.drawable.ic_cloud_blue_48dp)
        syncDrawable.drawable = context.getDrawable(R.drawable.ic_sync_black_48dp)
        syncDrawable.fromDegrees = 0f
        syncDrawable.toDegrees = 360f
    }

    override fun onDraw(canvas: Canvas) {
        cloudDrawable?.setBounds(0, 0, width, width)
        syncDrawable.setBounds(
                ((width - width * syncSizeRation) / 2).toInt(),
                ((width - width * syncSizeRation * 0.8) / 2).toInt(),
                (width * syncSizeRation + (width - width * syncSizeRation) / 2).toInt(),
                (width * syncSizeRation + (width - width * syncSizeRation * 0.8) / 2).toInt())

        syncDrawable.level -= 200
        cloudDrawable?.draw(canvas)
        syncDrawable.draw(canvas)
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 500
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)

        val width = when (widthMode) {
            View.MeasureSpec.EXACTLY -> widthSize
            View.MeasureSpec.AT_MOST -> Math.max(desiredWidth, widthSize)
            View.MeasureSpec.UNSPECIFIED -> desiredWidth
            else -> 0
        }

        setMeasuredDimension(width, width)
    }
}