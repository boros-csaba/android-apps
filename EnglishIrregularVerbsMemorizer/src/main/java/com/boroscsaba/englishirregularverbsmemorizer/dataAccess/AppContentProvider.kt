package com.boroscsaba.englishirregularverbsmemorizer.dataAccess

import com.boroscsaba.commonlibrary.ContentProvider
import com.boroscsaba.commonlibrary.settings.SettingOptionMapper
import com.boroscsaba.commonlibrary.tutorial.TutorialLogItemMapper
import com.boroscsaba.dataaccess.UriMapping
import com.boroscsaba.englishirregularverbsmemorizer.BuildConfig


/**
* Created by boros on 3/4/2018.
*/
class AppContentProvider: ContentProvider(DataAccessProvider) {

    override val authority = BuildConfig.CONTENT_AUTHORITY
    override val uriMappings = arrayOf(
        UriMapping(TutorialLogItemMapper.DATABASE_TABLE_NAME),
        UriMapping(SettingOptionMapper.DATABASE_TABLE_NAME),
        UriMapping(AnswerMapper.DATABASE_TABLE_NAME),
        UriMapping(DailyResultMapper.DATABASE_TABLE_NAME)
    )
}