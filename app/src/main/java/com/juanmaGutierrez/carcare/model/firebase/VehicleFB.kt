package com.juanmaGutierrez.carcare.model.firebase

import android.content.Context
import android.net.Uri

/**
 * Represents a vehicle in Firebase.
 * @property available Indicates if the vehicle is available.
 * @property brand The brand of the vehicle.
 * @property category The category of the vehicle.
 * @property created The creation date of the vehicle.
 * @property imageURL The URL of the image of the vehicle.
 * @property model The model of the vehicle.
 * @property plate The plate number of the vehicle.
 * @property registrationDate The registration date of the vehicle.
 * @property spents The list of expenses associated with the vehicle.
 * @property userId The ID of the user associated with the vehicle.
 * @property vehicleId The ID of the vehicle.
 */
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
    /**
     * Returns a string representation of the vehicle.
     */
    override fun toString(): String {
        return "Category: $category\nBrand: $brand\nModel: $model\nPlate: $plate\nAvailable: $available\nRegistration date: ${registrationDate}\nCreated: $created\nImageURL: $imageURL\nVehicle ID: $vehicleId\nNº Spents: ${spents.size}\nUserID: $userId"
    }
}

/**
 * Represents a pack of vehicle image data to be stored in Firebase.
 * @property context The context of the application.
 * @property uri The URI of the vehicle image.
 * @property name The name of the vehicle.
 * @property vehicle The vehicle data associated with the image.
 */
data class VehicleImagePackToFB(
    val context: Context,
    val uri: Uri,
    val name: String,
    val vehicle: VehicleFB,
) {
    /**
     * Returns a string representation of the vehicle image pack.
     */
    override fun toString(): String {
        return "Context: $context\nUri: $uri\nName: $name\nVehicle: $vehicle"
    }
}