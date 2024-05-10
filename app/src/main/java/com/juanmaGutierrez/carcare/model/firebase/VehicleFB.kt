package com.juanmaGutierrez.carcare.model.firebase

import android.content.Context
import android.net.Uri

data class VehicleFB(
    val available: Boolean,
    val brand: String,
    val category: String,
    val created: String,
    val imageURL: String? = null,
    val model: String,
    val plate: String,
    val registrationDate: String,
    val spents: List<SpentFB>,
    val userId: String,
    val vehicleId: String
) {
    override fun toString(): String {
        return "Category: $category\nBrand: $brand\nModel: $model\nPlate: $plate\nAvailable: $available\nRegistration date: ${registrationDate}\nCreated: $created\nImageURL: $imageURL\nVehicle ID: $vehicleId\nNº Spents: ${spents.size}\nUserID: $userId"
    }
}

data class VehicleImagePackToFB(
    val context: Context,
    val uri: Uri,
    val name: String,
    val vehicle: VehicleFB,
) {
    override fun toString(): String {
        return "Context: $context\nUri: $uri\nName: $name\nVehicle: $vehicle"
    }
}