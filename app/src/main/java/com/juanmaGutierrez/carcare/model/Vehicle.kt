package com.juanmaGutierrez.carcare.model

import com.google.firebase.firestore.DocumentReference

data class Vehicle(
    var available: Boolean,
    var brand: String,
    var category: String,
    var created: String,
    var model: String,
    var plate: String,
    var ref: DocumentReference,
    var registrationDate: String,
    var vehicleId: String
)
