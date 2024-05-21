package com.juanmaGutierrez.carcare.mapping

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.juanmaGutierrez.carcare.localData.entity.VehicleEntity
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.firebase.SpentFB
import com.juanmaGutierrez.carcare.model.firebase.UserFB
import com.juanmaGutierrez.carcare.model.firebase.VehicleFB
import com.juanmaGutierrez.carcare.model.localData.Provider
import com.juanmaGutierrez.carcare.model.localData.Spent
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

fun mapVehicleFBToVehicle(document: DocumentSnapshot): VehicleFB {
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

fun mapHashVehiclesToList(vehiclesList: List<HashMap<String, Any>>): List<VehiclePreview> {
    return vehiclesList.map { data ->
        VehiclePreview(
            data["available"] as Boolean,
            data["brand"] as String,
            data["category"] as String,
            data["created"] as String,
            data["imageURL"] as String?,
            data["model"] as String,
            data["plate"] as String,
            data["ref"] as DocumentReference,
            data["registrationDate"] as String,
            data["vehicleId"] as String
        )
    }
}

fun mapUserToUserFB(user: User, uid: String): UserFB {
    val currentTimeStamp = getTimestamp()
    return UserFB(
        currentTimeStamp,
        user.email,
        user.name,
        user.username,
        Constants.DEFAULT_ROLE,
        user.surname,
        uid,
        emptyList(),
    )
}

fun mapProviderFBtoProvider(data: Map<String, List<Map<String, String>>>): MutableList<Provider> {
    val providersList = mutableListOf<Provider>()
    val providersData = data["providers"]
    providersData?.forEach { providerData ->
        val provider = Provider()
        provider.category = providerData["category"].toString()
        provider.created = providerData["created"].toString()
        provider.name = providerData["name"].toString()
        provider.phone = providerData["phone"].toString()
        provider.providerId = providerData["providerId"].toString()
        providersList.add(provider)
    }
    return providersList
}

fun mapSpentListFBToSpentList(hashMapSpent: List<Map<String, Any>>): List<SpentFB> {
    return hashMapSpent.mapNotNull { rawSpent ->
        try {
            SpentFB(
                (rawSpent["amount"] as? Number)?.toDouble() ?: 0.0,
                rawSpent["created"]?.toString() ?: "",
                rawSpent["date"]?.toString() ?: "",
                rawSpent["observations"]?.toString() ?: "",
                rawSpent["providerId"]?.toString() ?: "",
                rawSpent["providerName"]?.toString() ?: "",
                rawSpent["spentId"]?.toString() ?: ""
            )
        } catch (e: Exception) {
            Log.e(Constants.TAG_ERROR, "Error mapping spent: ${e.message}")
            null
        }
    }
}

fun mapProvidersListRawToProvidersList(providers: List<Map<String, Any>>): MutableList<Provider> {
    return providers.map { p ->
        Provider(
            category = p["category"] as String,
            created = p["created"] as String,
            name = p["name"] as String,
            phone = p["phone"] as String,
            providerId = p["providerId"] as String
        )
    }.toMutableList()
}

fun mapSpentFBToSpent(spentFB: SpentFB): Spent {
    return Spent(
        spentFB.amount,
        spentFB.created,
        spentFB.date,
        spentFB.observations,
        spentFB.providerId,
        spentFB.providerName,
        spentFB.spentId
    )
}