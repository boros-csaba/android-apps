package com.boroscsaba.commonlibrary

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.boroscsaba.dataaccess.PersistentObject
import com.boroscsaba.dataaccess.RepositoryBaseBase

class LogicBase<T: PersistentObject>(private val repository: RepositoryBaseBase<T>) {

    fun getByIdOrDefault(id: Int, liveData: MutableLiveData<T>) {
        AsyncTask().execute({
            val result = getByIdOrDefault(id)
            Handler(Looper.getMainLooper()).post { liveData.value = result }
        })
    }

    private fun getByIdOrDefault(id: Int): T {
        return repository.getObjectByIdOrDefault(id)
    }

    fun saveChanges(item: T, changes: HashMap<String, Any>, onlyOneObjectADay: Boolean) {
        var savedItem = item
        if (onlyOneObjectADay) {
            val objects = repository.getObjects(null, null, null)
            objects.forEach { itemObject ->
                if (itemObject.getDailyUniqueValue() == savedItem.getDailyUniqueValue() && Utils.areOnSameDay(itemObject.getEffectiveDate(), savedItem.getEffectiveDate())) {
                    savedItem = itemObject
                }
            }
        }
        repository.mapChanges(savedItem, changes)
        repository.upsert(savedItem, false)
    }

    fun delete(item: T) {
        repository.delete(item.id, false)
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <T:PersistentObject> getInstance(classType: Class<T>, context: Context): LogicBase<T> {
            val objectInstance = classType.constructors.first().newInstance(context) as T
            return LogicBase(objectInstance.getRepository()!!) as LogicBase<T>
        }
    }
}