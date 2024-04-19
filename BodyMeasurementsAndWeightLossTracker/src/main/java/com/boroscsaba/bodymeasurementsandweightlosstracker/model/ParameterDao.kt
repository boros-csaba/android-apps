package com.boroscsaba.bodymeasurementsandweightlosstracker.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.*
import com.boroscsaba.commonlibrary.IDao

@Dao
abstract class ParameterDao: IDao<Parameter> {

    @Query("select * from Parameter where Id = :id")
    abstract override fun getAsync(id: Int): LiveData<Parameter?>

    @Query("select * from Parameter")
    abstract override fun getAll(): List<Parameter>

    @Query("select * from Parameter")
    abstract fun getAllParameters(): LiveData<List<Parameter>>

    @Query("select * from Measurements order by log_date desc")
    abstract fun getAllMeasurements(): LiveData<List<Measurement>>

    override fun getAllAsync(): LiveData<List<Parameter>> {
        return Transformations.switchMap(getAllParameters()) { parameters ->
            Transformations.map(getAllMeasurements()) { measurements ->
                parameters.map{ parameter -> parameter.withMeasurements(measurements) }
            }
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract override fun insert(entity: Parameter): Long

    @Update
    abstract override fun update(entity: Parameter)

    @Delete
    abstract override fun delete(entity: Parameter)
}