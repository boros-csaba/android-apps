package com.boroscsaba.commonlibrary

import com.boroscsaba.dataaccess.ContentProviderBase
import com.boroscsaba.dataaccess.DataAccessProviderBase

abstract class ContentProvider(dataAccessProvider: DataAccessProviderBase): ContentProviderBase(dataAccessProvider)