package com.boroscsaba.commonlibrary

import androidx.lifecycle.LiveData
import androidx.room.*
import com.boroscsaba.dataaccess.EntityBase

interface IDao<T: EntityBase> {

    fun getAsync(id: Int): LiveData<T?>
    fun getAll(): List<T>
    fun getAllAsync(): LiveData<List<T>>

    @Insert(onConflict = OnConflictStrategy.IGNORE) fun insert(entity: T): Long
    @Update fun update(entity: T)
    @Delete fun delete(entity: T)
}