package com.boroscsaba.myapplication.dataAccess

import android.content.ContentValues
import android.net.Uri
import com.boroscsaba.commonlibrary.ContentProvider
import com.boroscsaba.commonlibrary.settings.SettingOptionMapper
import com.boroscsaba.commonlibrary.tutorial.TutorialLogItemMapper
import com.boroscsaba.dataaccess.UriMapping
import com.boroscsaba.myapplication.BuildConfig


/**
* Created by boros on 3/4/2018.
*/
class AppContentProvider: ContentProvider(DataAccessProvider) {

    override val authority = BuildConfig.CONTENT_AUTHORITY
    override val uriMappings = arrayOf(
        UriMapping(GoalMapper.DATABASE_TABLE_NAME),
        UriMapping(TransactionMapper.DATABASE_TABLE_NAME),
        UriMapping(TutorialLogItemMapper.DATABASE_TABLE_NAME),
        UriMapping(SettingOptionMapper.DATABASE_TABLE_NAME)
    )

    override fun notifyRelatedObjects(uri: Uri, values: ContentValues?) {
        val context = context ?: return
        super.notifyRelatedObjects(uri, values)
        when (getTableName(uri)) {
            TransactionMapper(context).tableName -> {
                val goalId = values?.getAsInteger("goal_id")
                if (goalId != null) {
                    context.contentResolver.notifyChange(GoalMapper(context).getUri(goalId), null)
                }
                else {
                    context.contentResolver.notifyChange(GoalMapper(context).getUri(), null)
                }
            }
        }
    }
}