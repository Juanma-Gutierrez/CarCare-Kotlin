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
    val vehicleEntities = vehicles.map { rawVehicle ->
        val refVehicle = "/vehicle/" + rawVehicle["vehicleId"].toString()
        VehicleEntity(
            available = rawVehicle["available"] as Boolean,
            brand = rawVehicle["brand"].toString(),
            category = rawVehicle["category"].toString(),
            created = rawVehicle["created"].toString(),
            imageURL = rawVehicle["imageURL"].toString(),
            model = rawVehicle["model"].toString(),
            plate = rawVehicle["plate"].toString(),
            ref = refVehicle,
            registrationDate = rawVehicle["registrationDate"].toString(),
            vehicleId = rawVehicle["vehicleId"].toString(),
        )
    }
    return vehicleEntities
}

fun mapVehiclesListEntityToVehiclesList(vehicles: List<VehicleEntity>): List<VehiclePreview> {
    val vehiclesList = vehicles.map { rawVehicle ->
        val docRef = FirebaseFirestore.getInstance().document(rawVehicle.ref)
        VehiclePreview(
            available = rawVehicle.available,
            brand = rawVehicle.brand,
            category = rawVehicle.category,
            created = rawVehicle.created,
            imageURL = rawVehicle.imageURL,
            model = rawVehicle.model,
            plate = rawVehicle.plate,
            ref = docRef,
            registrationDate = rawVehicle.registrationDate,
            vehicleId = rawVehicle.vehicleId,
        )
    }
    return vehiclesList
}

fun mapVehicleFBToVehicle(document: DocumentSnapshot): VehicleFB {
    val rawVehicleFB = document.data!!
    return VehicleFB(
        rawVehicleFB["available"] as Boolean,
        rawVehicleFB["brand"] as String,
        rawVehicleFB["category"] as String,
        rawVehicleFB["created"] as String,
        rawVehicleFB["imageURL"] as String?,
        rawVehicleFB["model"] as String,
        rawVehicleFB["plate"] as String,
        rawVehicleFB["registrationDate"] as String,
        rawVehicleFB["spents"] as List<SpentFB>,
        rawVehicleFB["userId"] as String,
        rawVehicleFB["vehicleId"] as String,
    )
}

fun mapVehicleToVehiclePreview(rawVehicle: VehicleFB): VehiclePreview {
    val vehiclePath = "/vehicle/${rawVehicle.vehicleId}"
    val db = FirebaseFirestore.getInstance()
    val docRef = db.document(vehiclePath)
    return VehiclePreview(
        rawVehicle.available,
        rawVehicle.brand,
        rawVehicle.category,
        rawVehicle.created,
        rawVehicle.imageURL,
        rawVehicle.model,
        rawVehicle.plate,
        docRef,
        rawVehicle.registrationDate,
        rawVehicle.vehicleId
    )
}

fun mapHashVehiclesToList(vehiclesList: List<HashMap<String, Any>>): List<VehiclePreview> {
    return vehiclesList.map { rawVehicle ->
        VehiclePreview(
            rawVehicle["available"] as Boolean,
            rawVehicle["brand"] as String,
            rawVehicle["category"] as String,
            rawVehicle["created"] as String,
            rawVehicle["imageURL"] as String?,
            rawVehicle["model"] as String,
            rawVehicle["plate"] as String,
            rawVehicle["ref"] as DocumentReference,
            rawVehicle["registrationDate"] as String,
            rawVehicle["vehicleId"] as String
        )
    }
}

fun mapUserToUserFB(rawUser: User, uid: String): UserFB {
    val currentTimeStamp = getTimestamp()
    return UserFB(
        currentTimeStamp,
        rawUser.email,
        rawUser.name,
        rawUser.username,
        Constants.DEFAULT_ROLE,
        rawUser.surname,
        uid,
        emptyList(),
    )
}

fun mapProviderFBtoProvider(data: Map<String, List<Map<String, String>>>): MutableList<Provider> {
    val providersList = mutableListOf<Provider>()
    val providersData = data["providers"]
    providersData?.forEach { rawProvider ->
        val providerToAdd = Provider(
            category = rawProvider["category"].toString(),
            created = rawProvider["created"].toString(),
            name = rawProvider["name"].toString(),
            phone = rawProvider["phone"].toString(),
            providerId = rawProvider["providerId"].toString(),
        )
        providersList.add(providerToAdd)
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
    return providers.map { rawProvider ->
        Provider(
            category = rawProvider["category"] as String,
            created = rawProvider["created"] as String,
            name = rawProvider["name"] as String,
            phone = rawProvider["phone"] as String,
            providerId = rawProvider["providerId"] as String
        )
    }.toMutableList()
}

fun mapSpentFBToSpent(rawSpentFB: SpentFB): Spent {
    return Spent(
        rawSpentFB.amount,
        rawSpentFB.created,
        rawSpentFB.date,
        rawSpentFB.observations,
        rawSpentFB.providerId,
        rawSpentFB.providerName,
        rawSpentFB.spentId
    )
}


fun mapHashMapSpentToSpent(rawSpentHashMap: HashMap<String, Any>): Spent {
    return Spent(
        amount = rawSpentHashMap["amount"] as Double,
        created = rawSpentHashMap["created"].toString(),
        date = rawSpentHashMap["date"].toString(),
        observations = rawSpentHashMap["observations"].toString(),
        providerId = rawSpentHashMap["providerId"].toString(),
        providerName = rawSpentHashMap["providerName"].toString(),
        spentId = rawSpentHashMap["spentId"].toString(),
    )
}