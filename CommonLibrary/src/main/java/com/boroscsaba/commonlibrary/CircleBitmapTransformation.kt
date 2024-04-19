package com.boroscsaba.commonlibrary

import android.graphics.*
import android.media.ThumbnailUtils
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

/**
* Created by boros on 3/14/2018.
*/
class CircleBitmapTransformation: BitmapTransformation() {

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val size = Math.max(outWidth, outHeight)
        val result = pool.get(size, size, Bitmap.Config.ARGB_8888)
        transform(toTransform, size, result)
        return result
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("CircleBitmapTransformation".toByteArray())
    }

    fun transform(bitmap: Bitmap, size: Int): Bitmap {
        val destination = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        transform(bitmap, size, destination)
        return destination
    }

    private fun transform(bitmap: Bitmap, size: Int, output: Bitmap) {
        val srcBitmap = ThumbnailUtils.extractThumbnail(bitmap, size, size)
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val r = size / 2f
        val rect = Rect(0, 0, size, size)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawCircle(r, r, r, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(srcBitmap, rect, rect, paint)
    }
}