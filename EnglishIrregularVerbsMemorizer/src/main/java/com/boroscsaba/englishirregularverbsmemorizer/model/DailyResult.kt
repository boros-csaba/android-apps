package com.boroscsaba.englishirregularverbsmemorizer.model

import android.content.Context
import com.boroscsaba.dataaccess.IObjectWithId
import com.boroscsaba.dataaccess.PersistentObject
import com.boroscsaba.dataaccess.RepositoryBaseBase
import com.boroscsaba.englishirregularverbsmemorizer.dataAccess.DailyResultRepository

class DailyResult(override val context: Context): PersistentObject(), IObjectWithId {
	override var id: Int = 0
	override var createdDate: Long = 0
	override var modifiedDate: Long = 0
	override var guid: String = ""
	var day: Long = 0
	var correctGuesses: Int = 0
	var missedGuesses: Int = 0
	var goalMet = false

	override fun getRepository(): RepositoryBaseBase<PersistentObject>? {
		@Suppress("UNCHECKED_CAST")
		return DailyResultRepository(context) as RepositoryBaseBase<PersistentObject>
	}
}