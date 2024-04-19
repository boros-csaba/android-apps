package com.boroscsaba.bodymeasurementsandweightlosstracker.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.*
import com.boroscsaba.commonlibrary.IDao

@Dao
abstract class MeasurementDao: IDao<Measurement> {

    @Query("select * from Measurements where Id = :id")
    abstract fun getMeasurementAsync(id: Int): LiveData<Measurement>

    @Query("select * from Parameter where Id = :id")
    abstract fun getParameter(id: Int): Parameter

    @Query("select * from Parameter where Id = :id")
    abstract fun getParameterAsync(id: Int?): LiveData<Parameter>

    override fun getAsync(id: Int): LiveData<Measurement?> {
        return Transformations.switchMap(getMeasurementAsync(id)) { measurement ->
            Transformations.map(getParameterAsync(measurement?.parameterId)) { parameter ->
                measurement?.withParameter(parameter)
            }
        }
    }

    @Query("select * from Measurements order by log_date desc")
    abstract fun getAllMeasurements(): LiveData<List<Measurement>>

    @Query("select * from Parameter")
    abstract fun getAllParameters(): LiveData<List<Parameter>>

    override fun getAllAsync(): LiveData<List<Measurement>> {
        return Transformations.switchMap(getAllMeasurements()) { measurements ->
            Transformations.map(getAllParameters()) { parameters ->
                measurements.map{ measurement -> measurement.withParameter(parameters) }
            }
        }
    }

    @Query("select * from Measurements order by log_date desc")
    abstract override fun getAll(): List<Measurement>

}