package com.boroscsaba.commonlibrary

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


/**
 * Created by Boros Csaba
 */
class GalleryImageHelper(val context:Activity) {
    private val requestPhotoGallery = 5674

    fun startGetImageFromGallery() {
        if (!hasPermissions()) {
            askForPermissions()
            return
        }

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        //todo translate
        context.startActivityForResult(Intent.createChooser(intent, "Select image"), requestPhotoGallery)
    }

    fun getImageFromGalleryCallback(requestCode: Int, resultCode: Int, data: Intent?) : Uri? {
        if (requestCode != requestPhotoGallery || resultCode != Activity.RESULT_OK) {
            return null
        }
        return data?.data
    }

    private fun hasPermissions() : Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
    }

    private fun askForPermissions() {
        //todo start gallery after permissions are granted
        ActivityCompat.requestPermissions(context,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
    }
}