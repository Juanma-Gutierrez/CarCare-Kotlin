package com.juanmaGutierrez.carcare.localData.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

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
