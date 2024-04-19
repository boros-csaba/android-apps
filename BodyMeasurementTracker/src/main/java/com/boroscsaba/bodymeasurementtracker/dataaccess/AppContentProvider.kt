package com.boroscsaba.bodymeasurementtracker.dataaccess

import android.content.ContentValues
import android.net.Uri
import com.boroscsaba.bodymeasurementtracker.BuildConfig
import com.boroscsaba.commonlibrary.ContentProvider
import com.boroscsaba.commonlibrary.settings.SettingOptionMapper
import com.boroscsaba.commonlibrary.tutorial.TutorialLogItemMapper
import com.boroscsaba.dataaccess.UriMapping


/**
* Created by boros on 3/4/2018.
*/
class AppContentProvider: ContentProvider(DataAccessProvider) {

    override val authority = BuildConfig.CONTENT_AUTHORITY
    override val uriMappings = arrayOf(
            UriMapping(ParameterMapper.DATABASE_TABLE_NAME),
            UriMapping(MeasurementLogEntryMapper.DATABASE_TABLE_NAME),
            UriMapping(TutorialLogItemMapper.DATABASE_TABLE_NAME),
            UriMapping(SettingOptionMapper.DATABASE_TABLE_NAME)
    )

    override fun notifyRelatedObjects(uri: Uri, values: ContentValues?) {
        val context = context ?: return
        super.notifyRelatedObjects(uri, values)
        when (getTableName(uri)) {
            MeasurementLogEntryMapper(context).tableName -> {
                val parameterId = values?.getAsInteger("parameter_id")
                if (parameterId != null) {
                    context.contentResolver.notifyChange(ParameterMapper(context).getUri(parameterId), null)
                }
                else {
                    context.contentResolver.notifyChange(ParameterMapper(context).getUri(), null)
                }
            }
        }
    }
}