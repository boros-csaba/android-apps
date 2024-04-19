package com.boroscsaba.commonlibrary.helpers

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import android.content.ClipData
import android.net.Uri

/**
 * Created by Boros Csaba
 */

class CameraImageHelper(val context: Activity) {
    private val requestImageCapture = 6597
    private var uri : Uri? = null

    fun startGetImageFromCamera(filesAuthority : String) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val filePath = File(context.filesDir, "internal")
        val file = File(filePath, "newImage.jpg")
        if (!file.exists()) {
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            file.createNewFile()
        }
        uri =  FileProvider.getUriForFile(context, filesAuthority, file)
        intent.clipData = ClipData.newRawUri(null, uri)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivityForResult(intent, requestImageCapture)
        }
    }

    fun getImageFromCameraCallback(requestCode: Int, resultCode: Int): Uri? {
        if (requestCode != requestImageCapture || resultCode != Activity.RESULT_OK) {
            return null
        }
        return uri
    }
}