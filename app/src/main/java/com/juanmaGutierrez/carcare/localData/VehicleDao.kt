package com.juanmaGutierrez.carcare.localData

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface VehicleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicles(vehicles: List<VehicleEntity>)

    @Query("SELECT * FROM vehicles")
    suspend fun getVehicles(): List<VehicleEntity>

    @Transaction
    suspend fun replaceAllVehicles(vehicles: List<VehicleEntity>) {
        clearVehicles()
        insertVehicles(vehicles)
    }

    @Query("DELETE FROM vehicles")
    suspend fun clearVehicles()
}
