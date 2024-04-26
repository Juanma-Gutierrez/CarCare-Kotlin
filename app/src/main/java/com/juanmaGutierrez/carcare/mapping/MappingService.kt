package com.juanmaGutierrez.carcare.mapping

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.juanmaGutierrez.carcare.localData.entity.VehicleEntity
import com.juanmaGutierrez.carcare.model.firebase.SpentFB
import com.juanmaGutierrez.carcare.model.firebase.VehicleFB
import com.juanmaGutierrez.carcare.model.localData.VehiclePreview

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

fun mapVehiclesListEntityToVehiclesList(vehicles: List<VehicleEntity>): List<VehiclePreview> {
    val vehiclesList = vehicles.map { vehicleData ->
        val docRef = FirebaseFirestore.getInstance().document(vehicleData.ref)
        VehiclePreview(
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

 fun mapDocumentDataToVehicle(document: DocumentSnapshot): VehicleFB {
    val data = document.data ?: throw IllegalArgumentException("Document data was null or empty")
    return VehicleFB(
        data["available"] as Boolean,
        data["brand"] as String,
        data["category"] as String,
        data["created"] as String,
        data["model"] as String,
        data["plate"] as String,
        data["registrationDate"] as String,
        data["spents"] as List<SpentFB>,
        data["userId"] as String,
        data["vehicleId"] as String,
    )
}