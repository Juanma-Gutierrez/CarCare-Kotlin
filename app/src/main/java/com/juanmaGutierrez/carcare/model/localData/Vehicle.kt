package com.juanmaGutierrez.carcare.model.localData

import com.google.firebase.firestore.DocumentReference
import com.juanmaGutierrez.carcare.localData.entity.VehicleEntity
import java.util.Random
import java.util.UUID

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
