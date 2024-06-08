package com.juanmaGutierrez.carcare.localData.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.juanmaGutierrez.carcare.localData.entity.VehicleEntity

/**
 * Data Access Object (DAO) for interacting with the vehicle entities in the database
 */
@Dao
interface VehicleDao {
    /**
     * Inserts a list of vehicles into the database, replacing any existing vehicles with the same primary key
     * @param vehicles: The list of vehicles to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicles(vehicles: List<VehicleEntity>)

    /**
     * Retrieves all vehicles from the database
     * @return List<VehicleEntity>: The list of vehicles stored in the database
     */
    @Query("SELECT * FROM vehicles")
    suspend fun getVehicles(): List<VehicleEntity>

    /**
     * Replaces all vehicles in the database with the given list of vehicles
     * This operation is performed within a single transaction
     * @param vehicles: The list of vehicles to replace all existing vehicles with
     */
    @Transaction
    suspend fun replaceAllVehicles(vehicles: List<VehicleEntity>) {
        clearVehicles()
        insertVehicles(vehicles)
    }

    /**
     * Deletes all vehicles from the database
     */
    @Query("DELETE FROM vehicles")
    suspend fun clearVehicles()
}
