package com.juanmaGutierrez.carcare.model.localData

import com.google.firebase.firestore.DocumentReference

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
){
    override fun toString(): String {
        return "Available: $available\nBrand: $brand\nCategory: $category\nCreated: $created\nImageURL: $imageURL\nModel: $model\nPlate: $plate\nRef: $ref\nRegistrationDate: $registrationDate\nVehicleId: $vehicleId"
    }
}


