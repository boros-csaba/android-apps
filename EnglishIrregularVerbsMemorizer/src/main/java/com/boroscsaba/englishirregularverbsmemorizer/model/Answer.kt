package com.boroscsaba.englishirregularverbsmemorizer.model

import android.content.Context
import com.boroscsaba.dataaccess.IObjectWithId
import com.boroscsaba.dataaccess.PersistentObject
import com.boroscsaba.dataaccess.PropertyEditor
import com.boroscsaba.dataaccess.RepositoryBaseBase
import com.boroscsaba.englishirregularverbsmemorizer.dataAccess.AnswerRepository

class Answer(override val context: Context): PersistentObject(), IObjectWithId {
	override var id: Int = 0
	override var createdDate: Long = 0
	override var modifiedDate: Long = 0
	override var guid: String = ""
    var verbId: Int = 0
	var correctAnswers: Int = 0
	var wrongAnswers: Int = 0
    var hidden = false

	override fun getRepository(): RepositoryBaseBase<PersistentObject>? {
		@Suppress("UNCHECKED_CAST")
		return AnswerRepository(context) as RepositoryBaseBase<PersistentObject>
	}

	override fun getPropertyEditors(): ArrayList<PropertyEditor> {
		return ArrayList()
	}
}