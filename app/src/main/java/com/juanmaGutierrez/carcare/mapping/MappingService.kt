package com.juanmaGutierrez.carcare.mapping

import com.google.firebase.firestore.DocumentReference
import com.juanmaGutierrez.carcare.localData.VehicleEntity
import com.juanmaGutierrez.carcare.model.Vehicle

fun mapVehiclesListEntity(vehicles: List<Map<String, Any>>): List<VehicleEntity> {
    val vehicleEntities = vehicles.map { vehicleData ->
        VehicleEntity(
            available = vehicleData["available"] as Boolean,
            brand = vehicleData["brand"].toString(),
            category = vehicleData["category"].toString(),
            created = vehicleData["created"].toString(),
            model = vehicleData["model"].toString(),
            plate = vehicleData["plate"].toString(),
            ref = vehicleData["ref"].toString(),
            registrationDate = vehicleData["registrationDate"].toString(),
            userId = vehicleData["userId"].toString(),
            vehicleId = vehicleData["vehicleId"].toString(),
        )
    }
    return vehicleEntities
}

fun mapVehiclesList(vehicles: List<Map<String, Any>>): List<Vehicle> {
    val vehiclesList = vehicles.map { vehicleData ->
        Vehicle(
            available = vehicleData["available"] as Boolean,
            brand = vehicleData["brand"].toString(),
            category = vehicleData["category"].toString(),
            created = vehicleData["created"].toString(),
            model = vehicleData["model"].toString(),
            plate = vehicleData["plate"].toString(),
            ref = vehicleData["ref"] as DocumentReference,
            registrationDate = vehicleData["registrationDate"].toString(),
            vehicleId = vehicleData["vehicleId"].toString(),
        )
    }
    return vehiclesList
}