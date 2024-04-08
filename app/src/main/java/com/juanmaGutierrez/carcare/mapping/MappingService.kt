package com.juanmaGutierrez.carcare.mapping

import com.google.firebase.firestore.FirebaseFirestore
import com.juanmaGutierrez.carcare.localData.entity.VehicleEntity
import com.juanmaGutierrez.carcare.model.localData.Vehicle

fun mapVehiclesListRawToVehicleEntityList(vehicles: List<Map<String, Any>>): List<VehicleEntity> {
    val vehicleEntities = vehicles.map { vehicleData ->
        val refVehicle = "/vehicle/" + vehicleData["vehicleId"].toString()
        VehicleEntity(
            available = vehicleData["available"] as Boolean,
            brand = vehicleData["brand"].toString(),
            category = vehicleData["category"].toString(),
            created = vehicleData["created"].toString(),
            model = vehicleData["model"].toString(),
            plate = vehicleData["plate"].toString(),
            ref = refVehicle,
            registrationDate = vehicleData["registrationDate"].toString(),
            userId = vehicleData["userId"].toString(),
            vehicleId = vehicleData["vehicleId"].toString(),
        )
    }
    return vehicleEntities
}

fun mapVehiclesListEntityToVehiclesList(vehicles: List<VehicleEntity>): List<Vehicle> {
    val vehiclesList = vehicles.map { vehicleData ->
        val docRef = FirebaseFirestore.getInstance().document(vehicleData.ref)
        Vehicle(
            available = vehicleData.available,
            brand = vehicleData.brand,
            category = vehicleData.category,
            created = vehicleData.created,
            model = vehicleData.model,
            plate = vehicleData.plate,
            ref = docRef,
            registrationDate = vehicleData.registrationDate,
            vehicleId = vehicleData.vehicleId,
        )
    }
    return vehiclesList
}