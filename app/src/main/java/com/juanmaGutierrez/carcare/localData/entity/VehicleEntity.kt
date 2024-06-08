package com.juanmaGutierrez.carcare.localData.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class representing a vehicle stored in the local database
 * @param vehicleId: The unique identifier of the vehicle
 * @param available: Flag indicating whether the vehicle is available or not
 * @param brand: The brand of the vehicle
 * @param category: The category of the vehicle (e.g., car, motorcycle)
 * @param created: The creation date of the vehicle record
 * @param imageURL: The URL of the vehicle's image
 * @param model: The model of the vehicle
 * @param plate: The license plate number of the vehicle
 * @param ref: A reference field for the vehicle
 * @param registrationDate: The registration date of the vehicle
 */
@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey val vehicleId: String,
    val available: Boolean,
    val brand: String,
    val category: String,
    val created: String,
    val imageURL: String?,
    val model: String,
    val plate: String,
    val ref: String,
    val registrationDate: String,
)
