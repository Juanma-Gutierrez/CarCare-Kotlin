package com.juanmaGutierrez.carcare.localData.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey val vehicleId: String,
    val userId: String,
    val ref: String,
    val created: String,
    val registrationDate: String,
    val available: Boolean,
    val model: String,
    val plate: String,
    val category: String,
    val brand: String
)
