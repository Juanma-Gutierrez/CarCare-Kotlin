package com.juanmaGutierrez.carcare.model.localData

import com.google.firebase.firestore.DocumentReference

/**
 * Data class representing a preview of a vehicle.
 */
data class VehiclePreview(
    var available: Boolean,
    var brand: String,
    var category: String,
    var created: String,
    var imageURL: String? = null,
    var model: String,
    var plate: String,
    var ref: DocumentReference,
    var registrationDate: String,
    var vehicleId: String,
) {
    /**
     * Returns a string representation of the VehiclePreview.
     */
    override fun toString(): String {
        return "Available: $available\nBrand: $brand\nCategory: $category\nCreated: $created\nImageURL: $imageURL\nModel: $model\nPlate: $plate\nRef: $ref\nRegistrationDate: $registrationDate\nVehicleId: $vehicleId"
    }
}