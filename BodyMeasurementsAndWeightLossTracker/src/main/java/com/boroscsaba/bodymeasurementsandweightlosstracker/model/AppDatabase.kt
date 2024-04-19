package com.boroscsaba.bodymeasurementsandweightlosstracker.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Measurement::class, Parameter::class], version = 4)
abstract class AppDatabase: RoomDatabase() {

    abstract fun measurementDao(): MeasurementDao
    abstract fun parameterDao(): ParameterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "MeasurementsDB").build()
                INSTANCE = instance
                return instance
            }
        }
    }
}