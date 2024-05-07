package com.juanmaGutierrez.carcare.mapping

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.juanmaGutierrez.carcare.localData.entity.VehicleEntity
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.firebase.SpentFB
import com.juanmaGutierrez.carcare.model.firebase.UserFB
import com.juanmaGutierrez.carcare.model.firebase.VehicleFB
import com.juanmaGutierrez.carcare.model.localData.User
import com.juanmaGutierrez.carcare.model.localData.VehiclePreview
import com.juanmaGutierrez.carcare.service.getTimestamp

fun mapVehiclesListRawToVehicleEntityList(vehicles: List<Map<String, Any>>): List<VehicleEntity> {
    val vehicleEntities = vehicles.map { vehicleData ->
        val refVehicle = "/vehicle/" + vehicleData["vehicleId"].toString()
        VehicleEntity(
            available = vehicleData["available"] as Boolean,
            brand = vehicleData["brand"].toString(),
            category = vehicleData["category"].toString(),
            created = vehicleData["created"].toString(),
            imageURL = vehicleData["imageURL"].toString(),
            model = vehicleData["model"].toString(),
            plate = vehicleData["plate"].toString(),
            ref = refVehicle,
            registrationDate = vehicleData["registrationDate"].toString(),
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
            imageURL = vehicleData.imageURL,
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
        data["imageURL"] as String?,
        data["model"] as String,
        data["plate"] as String,
        data["registrationDate"] as String,
        data["spents"] as List<SpentFB>,
        data["userId"] as String,
        data["vehicleId"] as String,
    )
}

fun mapVehicleToVehiclePreview(vehicle: VehicleFB): VehiclePreview {
    val vehiclePath = "/vehicle/${vehicle.vehicleId}"
    val db = FirebaseFirestore.getInstance()
    val docRef = db.document(vehiclePath)
    return VehiclePreview(
        vehicle.available,
        vehicle.brand,
        vehicle.category,
        vehicle.created,
        vehicle.imageURL,
        vehicle.model,
        vehicle.plate,
        docRef,
        vehicle.registrationDate,
        vehicle.vehicleId
    )
}

fun mapHashVehiclesToList(existingVehiclesData: List<HashMap<String, Any>>): List<VehiclePreview> {
    return existingVehiclesData.map { vehicleData ->
        VehiclePreview(
            vehicleData["available"] as Boolean,
            vehicleData["brand"] as String,
            vehicleData["category"] as String,
            vehicleData["created"] as String,
            vehicleData["imageURL"] as String?,
            vehicleData["model"] as String,
            vehicleData["plate"] as String,
            vehicleData["ref"] as DocumentReference,
            vehicleData["registrationDate"] as String,
            vehicleData["vehicleId"] as String
        )
    }
}

fun mapUserToUserFB(user: User, uid: String): UserFB {
    val currentTimeStamp = getTimestamp()
    val data = UserFB(
        currentTimeStamp,
        user.email,
        user.name,
        user.username,
        Constants.DEFAULT_ROLE,
        user.surname,
        uid,
        emptyList(),
    )
    return data
}

