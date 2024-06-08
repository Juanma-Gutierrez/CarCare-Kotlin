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

/**
 * Maps a list of raw vehicle data to a list of VehicleEntity objects
 * @param vehicles: The list of raw vehicle data
 * @return List<VehicleEntity>: The list of mapped VehicleEntity objects
 */
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

/**
 * Maps a list of VehicleEntity objects to a list of VehiclePreview objects
 * @param vehicles: The list of VehicleEntity objects
 * @return List<VehiclePreview>: The list of mapped VehiclePreview objects
 */
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

/**
 * Maps a Firebase document snapshot containing vehicle data to a VehicleFB object
 * @param document: The Firebase document snapshot containing vehicle data
 * @return VehicleFB: The mapped VehicleFB object
 */
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

/**
 * Maps a VehicleFB object to a VehiclePreview object
 * @param rawVehicle: The VehicleFB object to map
 * @return VehiclePreview: The mapped VehiclePreview object
 */
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

/**
 * Maps a list of HashMaps containing raw vehicle data to a list of VehiclePreview objects
 * @param vehiclesList: The list of HashMaps containing raw vehicle data
 * @return List<VehiclePreview>: The list of mapped VehiclePreview objects
 */
fun mapHashVehiclesToListVehiclePreview(vehiclesList: List<HashMap<String, Any>>): List<VehiclePreview> {
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

/**
 * Maps a User object to a UserFB object
 * @param rawUser: The User object to map
 * @param uid: The user ID
 * @return UserFB: The mapped UserFB object
 */
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

/**
 * Maps a Firebase data snapshot containing provider data to a list of Provider objects
 * @param data: The Firebase data snapshot containing provider data
 * @return MutableList<Provider>: The mapped list of Provider objects
 */
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

/**
 * Maps a list of hash map objects containing spent data to a list of SpentFB objects
 * @param hashMapSpent: The list of hash map objects containing spent data
 * @return List<SpentFB>: The mapped list of SpentFB objects
 */
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

/**
 * Maps a list of hash map objects containing provider data to a list of Provider objects
 * @param providers: The list of hash map objects containing provider data
 * @return MutableList<Provider>: The mapped list of Provider objects
 */
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

/**
 * Maps a SpentFB object to a Spent object
 * @param rawSpentFB: The SpentFB object to map
 * @return Spent: The mapped Spent object
 */
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

/**
 * Maps a hash map object containing spent data to a SpentFB object
 * @param map: The hash map object containing spent data
 * @return SpentFB: The mapped SpentFB object
 */
fun mapHashMapSpentToSpent(map: HashMap<String, Any>): SpentFB {
    return SpentFB(
        amount = map["amount"] as? Double ?: 0.0,
        created = map["created"] as? String ?: "",
        date = map["date"] as? String ?: "",
        observations = map["observations"] as? String ?: "",
        providerId = map["providerId"] as? String ?: "",
        providerName = map["providerName"] as? String ?: "",
        spentId = map["spentId"] as? String ?: ""
    )
}