package com.boroscsaba.dataaccess

import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import java.io.File
import java.lang.Exception

/**
* Created by boros on 3/4/2018.
*/
abstract class ContentProviderBase(private val dataAccessProvider: DataAccessProviderBase): ContentProvider() {

    abstract val authority: String
    abstract val uriMappings: Array<UriMapping>
    protected var dataAccess: DataAccess? = null
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    override fun onCreate(): Boolean {
        dataAccess = dataAccessProvider.getDataAccess(this.context!!)
        createUriMatcher()
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        val tableName = getTableName(uri)
        val id = dataAccess!!.insert(tableName, values)
        notifyRelatedObjects(getUri(uri, id), values)
        return getUri(uri, id)
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        val tableName = getTableName(uri)
        val result = dataAccess!!.update(tableName, values, selection, selectionArgs)
        notifyRelatedObjects(getUri(uri, getId(uri).toIntOrNull()), values)
        return result
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor {
        val cursor = if (isById(uri)) {
            dataAccess!!.getObjectById(getTableName(uri), getId(uri), projection)
        }
        else {
            dataAccess!!.getObjects(getTableName(uri), projection, selection, selectionArgs, sortOrder)
        }
        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val result = dataAccess!!.deleteObject(getTableName(uri), selection, selectionArgs)
        notifyRelatedObjects(getUri(uri, null), null)
        return result
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        val file = File(context!!.filesDir, "goal_icon_${getId(uri)}.png")
        if (mode == "w") {
            file.createNewFile()
        }
        return try {
            ParcelFileDescriptor.open(file, if (mode == "w") ParcelFileDescriptor.MODE_READ_WRITE else ParcelFileDescriptor.MODE_READ_ONLY)
        }
        catch (e: Exception) {
            null
        }
    }

    override fun getType(uri: Uri): String {
        return ""
    }

    open fun notifyRelatedObjects(uri: Uri, values: ContentValues?) {
        context!!.contentResolver.notifyChange(getUri(uri, null), null)
    }

    private fun getUri(uri: Uri?, id: Int?): Uri {
        var newUri = Uri.parse("content://$authority/${getTableName(uri)}")
        if (id != null) {
            newUri = ContentUris.appendId(newUri.buildUpon(), id.toLong()).build()
        }
        return newUri
    }

    private fun createUriMatcher() {
        for (i in uriMappings.indices) {
            uriMatcher.addURI(authority, uriMappings[i].tableName, i)
            uriMatcher.addURI(authority, uriMappings[i].tableName + "/#", i + uriMappings.size)
        }
    }

    protected fun getTableName(uri: Uri?): String {
        return uriMappings[uriMatcher.match(uri) % uriMappings.size].tableName
    }

    private fun isById(uri: Uri?): Boolean {
        return uriMatcher.match(uri) >= uriMappings.size
    }

    private fun getId(uri: Uri?): String {
        return uri?.lastPathSegment ?: "0"
    }
}