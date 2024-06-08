package com.juanmaGutierrez.carcare.localData.DAO

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.juanmaGutierrez.carcare.localData.entity.VehicleEntity

/**
 * Database class for managing the vehicle entities using Room
 */
@Database(entities = [VehicleEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Retrieves the DAO (Data Access Object) for interacting with the vehicle entities
     * @return VehicleDao: The DAO for vehicle entities
     */
    abstract fun vehicleDao(): VehicleDao

    /**
     * Companion object for providing access to the database instance
     */
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Retrieves the database instance, creating it if necessary
         * @param context: The application context
         * @return AppDatabase: The database instance
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

