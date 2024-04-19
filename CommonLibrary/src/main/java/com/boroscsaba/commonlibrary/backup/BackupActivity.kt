package com.boroscsaba.commonlibrary.backup

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import android.widget.Button
import java.util.zip.ZipOutputStream
import java.io.*
import java.util.zip.ZipEntry
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.View
import android.widget.ProgressBar
import java.util.zip.ZipInputStream
import android.app.AlarmManager
import android.app.PendingIntent
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.activities.ActivityBase
import android.annotation.SuppressLint
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess


/**
* Created by boros on 3/18/2018.
*/
class BackupActivity: ActivityBase() {

    private val backupLoading = MutableLiveData<Boolean>()
    private val backupError = MutableLiveData<String>()
    private val restoreLoading = MutableLiveData<Boolean>()
    private val restoreError = MutableLiveData<String>()

    init {
        options.layout = R.layout.activity_backup
        options.toolbarId = R.id.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backupLoading.observe(this, Observer { value ->
            if (value != null) {
                findViewById<ProgressBar>(R.id.backupLoadingGif).visibility = if (value) { View.VISIBLE } else { View.INVISIBLE }
                findViewById<Button>(R.id.backupButton).visibility = if (!value) { View.VISIBLE } else { View.INVISIBLE }
                if (!value && (backupError.value == null || backupError.value?.isEmpty() == true)) {
                    Toast.makeText(applicationContext, getString(R.string.backup_created_toast), Toast.LENGTH_SHORT).show()
                }
            }
        })
        backupError.observe(this, Observer { value ->
            if (value != null && value.isNotEmpty()) {
                val backupButton = findViewById<Button>(R.id.backupButton)
                backupButton.text = getString(R.string.error)
                backupButton.isClickable = false
                val alertDialog = AlertDialog.Builder(this).create()
                alertDialog.setTitle(R.string.error)
                alertDialog.setMessage(value)
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK") { _, _ -> }
                alertDialog.show()
            }
        })
        restoreLoading.observe(this, Observer { value ->
            if (value != null) {
                findViewById<ProgressBar>(R.id.restoreLoadingGif).visibility = if (value) { View.VISIBLE } else { View.INVISIBLE }
                findViewById<Button>(R.id.restoreButton).visibility = if (!value) { View.VISIBLE } else { View.INVISIBLE }
                if (!value && (restoreError.value == null || restoreError.value?.isEmpty() == true)) {
                    val alertDialog = AlertDialog.Builder(this).create()
                    alertDialog.setTitle(R.string.done)
                    alertDialog.setMessage(getString(R.string.activity_backup_restart_app))
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK") { _, _ ->
                        val backupActivity = Intent(this, BackupActivity::class.java)
                        val pendingIntentId = 123456
                        val pendingIntent = PendingIntent.getActivity(this, pendingIntentId, backupActivity, PendingIntent.FLAG_CANCEL_CURRENT)
                        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent)
                        exitProcess(0)
                    }
                    alertDialog.show()
                }
            }
        })
        restoreError.observe(this, Observer { value ->
            if (value != null && value.isNotEmpty()) {
                val restoreButton = findViewById<Button>(R.id.restoreButton)
                restoreButton.text = getString(R.string.error)
                restoreButton.isClickable = false
                val alertDialog = AlertDialog.Builder(this).create()
                alertDialog.setTitle(R.string.error)
                alertDialog.setMessage(value)
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK") { _, _ -> }
                alertDialog.show()
            }
        })
    }

    override fun setListeners() {
        findViewById<Button>(R.id.backupButton).setOnClickListener { backup() }
        findViewById<Button>(R.id.restoreButton).setOnClickListener{ restore() }
    }

    @SuppressLint("SimpleDateFormat")
    private fun backup() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type ="*/*"
        intent.putExtra(Intent.EXTRA_TITLE, packageName.split('.').last() + "_backup_" + SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().time) + ".data")
        startActivityForResult(intent, SAVE_FILE_REQUEST_CODE)
        backupLoading.value = true
    }

    @SuppressLint("SimpleDateFormat")
    private fun createBackupFile(uri: Uri): String? {
        try {
            val fileDescriptor = contentResolver.openFileDescriptor(uri, "w") ?: return null
            val fileOutputStream = FileOutputStream(fileDescriptor.fileDescriptor)
            val output = ZipOutputStream(BufferedOutputStream(fileOutputStream))
            zipSubFolder(output, filesDir, filesDir.parent.length)
            zipSubFolder(output, File(applicationInfo.dataDir + "/databases"), File(applicationInfo.dataDir + "/databases").parent.length)
            output.close()
            fileOutputStream.close()
            fileDescriptor.close()
        }
        catch (e: Exception) {
            LoggingHelper.logException(e, this)
            return e.message
        }
        return null
    }

    private fun zipSubFolder(output: ZipOutputStream, folder: File, basePathLength: Int) {
        val buffer = 2048
        val fileList = folder.listFiles()
        var origin: BufferedInputStream?
        for (file in fileList) {
            if (file.name.startsWith("backup") ||
                file.name == ".Fabric"){
                continue
            }
            if (file.isDirectory) {
                zipSubFolder(output, file, basePathLength)
            }
            else {
                val data = ByteArray(buffer)
                val unmodifiedFilePath = file.path
                val relativePath = unmodifiedFilePath.substring(basePathLength)
                val fileInputStream = FileInputStream(unmodifiedFilePath)
                origin = BufferedInputStream(fileInputStream, buffer)
                val entry = ZipEntry(relativePath)
                output.putNextEntry(entry)
                var count = origin.read(data, 0, buffer)
                while (count != -1) {
                    output.write(data, 0, count)
                    count = origin.read(data, 0, buffer)
                }
                origin.close()
            }
        }
    }

    private fun restore() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(intent, RESTORE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == RESTORE_REQUEST_CODE && data != null) {
                val uri = data.data ?: return
                BackupAsyncTask(restoreLoading, restoreError).execute({ restore(uri) })
            }

            if (requestCode == SAVE_FILE_REQUEST_CODE && data != null) {
                val uri = data.data ?: return
                BackupAsyncTask(backupLoading, backupError).execute({ createBackupFile(uri) })
            }
        }
    }

    private fun restore(uri: Uri): String? {
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val outputStream = FileOutputStream(File(filesDir.absolutePath + "/backup.data"), false)
            val buffer = ByteArray(1024)
            var length = inputStream.read(buffer)
            while (length > 0) {
                outputStream.write(buffer, 0, length)
                length = inputStream.read(buffer)
            }
            outputStream.close()
            inputStream.close()
            unZip(File(filesDir.absolutePath + "/backup.data"), filesDir!!.parentFile)
        }
        catch (e: Exception) {
            LoggingHelper.logException(e, this)
            return e.message
        }
        return null

    }

    private fun unZip(zipFile: File, targetDirectory: File) {
        val zipInputStream = ZipInputStream(BufferedInputStream(FileInputStream(zipFile)))
        targetDirectory.deleteRecursively()
        zipInputStream.use { z ->
            var count: Int
            val buffer = ByteArray(8192)
            var zipEntry = z.nextEntry
            while (zipEntry != null) {
                val file = File(targetDirectory, zipEntry.name)
                val dir = if (zipEntry.isDirectory) file else file.parentFile
                if (!dir.isDirectory && !dir.mkdirs())
                    throw FileNotFoundException("Failed to ensure directory: " + dir.absolutePath)
                if (zipEntry.isDirectory)
                    continue
                val fileOutputStream = FileOutputStream(file, false)
                fileOutputStream.use { f ->
                    count = z.read(buffer)
                    while (count != -1) {
                        f.write(buffer, 0, count)
                        count = z.read(buffer)
                    }
                }
                zipEntry = z.nextEntry
            }
        }
    }

    private class BackupAsyncTask(private val loading: MutableLiveData<Boolean>, private val error: MutableLiveData<String>): android.os.AsyncTask<() -> String?, Any, String?>() {
        override fun doInBackground(vararg function: (() -> String?)): String? {
            return function[0]()
        }

        override fun onPostExecute(result: String?) {
            if (result != null && result.isNotEmpty()) {
                error.value = result
            }
            loading.value = false
        }
    }

    companion object {
        const val SAVE_FILE_REQUEST_CODE = 89
        const val RESTORE_REQUEST_CODE = 103
    }
}