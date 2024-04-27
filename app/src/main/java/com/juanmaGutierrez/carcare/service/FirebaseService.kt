package com.juanmaGutierrez.carcare.service

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.juanmaGutierrez.carcare.mapping.mapHashVehiclesToList
import com.juanmaGutierrez.carcare.mapping.mapUserToUserFB
import com.juanmaGutierrez.carcare.mapping.mapVehicleToVehiclePreview
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.localData.ItemLog
import com.juanmaGutierrez.carcare.model.localData.LogType
import com.juanmaGutierrez.carcare.model.localData.OperationLog
import com.juanmaGutierrez.carcare.model.localData.Providers
import com.juanmaGutierrez.carcare.model.localData.User
import com.juanmaGutierrez.carcare.model.Constants.Companion.ERROR_CREATE_USER_WITH_EMAIL
import com.juanmaGutierrez.carcare.model.Constants.Companion.ERROR_DATABASE
import com.juanmaGutierrez.carcare.model.Constants.Companion.TAG
import com.juanmaGutierrez.carcare.model.firebase.VehicleFB
import com.juanmaGutierrez.carcare.model.localData.VehiclePreview
import kotlinx.coroutines.CompletableDeferred
import java.util.concurrent.Executors

class FirebaseService {
    var user: FirebaseUser? = null
    var auth: FirebaseAuth? = null

    override fun toString(): String {
        var data = ""
        if (user != null) {
            data = "-FirebaseService-\nUSER ID: ${user!!.uid}\nUSER EMAIL: ${user!!.email}\n"
        }
        return data
    }

    companion object {
        private var instance: FirebaseService? = null

        fun getInstance(): FirebaseService {
            if (instance == null) {
                instance = FirebaseService()
            }
            return instance!!
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun fbSaveLog(itemLog: ItemLog) {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection(Constants.COLLECTION_LOG).document(Constants.COLLECTION_LOG_DOC)
    docRef.update(Constants.COLLECTION_LOG_ARRAYLIST, FieldValue.arrayUnion(itemLog)).addOnSuccessListener {}
        .addOnFailureListener { e ->
            val logMap = mapOf("logs" to listOf(itemLog))
            docRef.set(logMap)
            Log.e(TAG, ERROR_DATABASE, e)
        }
}

@RequiresApi(Build.VERSION_CODES.O)
fun fbRegisterUserAuth(user: User) {
    val auth = Firebase.auth
    auth.createUserWithEmailAndPassword(user.email, user.password)
        .addOnCompleteListener(Executors.newSingleThreadExecutor()) { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser?.uid ?: ""
                saveToLog(LogType.INFO, auth, OperationLog.CREATE_USER, Constants.REGISTER_SUCCESSFULLY)
                fbCreateUser(user, uid)
                fbCreateProviders(uid)
            } else {
                Log.e(TAG, ERROR_CREATE_USER_WITH_EMAIL, task.exception)
            }
        }
}

fun fbCreateUser(user: User, uid: String) {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection(Constants.FB_COLLECTION_USER).document(uid)
    val mappedUser = mapUserToUserFB(user, uid)
    docRef.set(mappedUser).addOnSuccessListener {}
        .addOnFailureListener { e -> Log.e(Constants.TAG_ERROR, Constants.FB_ERROR_DB_OPERATION, e) }
}

fun fbCreateProviders(uid: String) {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection(Constants.FB_COLLECTION_PROVIDER).document(uid)
    val providers = Providers(emptyList())
    docRef.set(providers).addOnSuccessListener {}
        .addOnFailureListener { e -> Log.e(Constants.TAG_ERROR, Constants.FB_ERROR_DB_OPERATION, e) }
}

@RequiresApi(Build.VERSION_CODES.O)
fun fbSetVehicle(vehicle: VehicleFB): Task<Void> {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection(Constants.FB_COLLECTION_VEHICLE).document(vehicle.vehicleId)
    val result = docRef.set(vehicle).addOnSuccessListener {
        val fb = FirebaseService.getInstance()
        saveToLog(LogType.INFO, fb.auth, OperationLog.SET_VEHICLE, Constants.LOG_SET_VEHICLE)
    }.addOnFailureListener { e -> Log.e(Constants.TAG_ERROR, Constants.FB_ERROR_DB_OPERATION, e) }
    return result
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun fbSetVehiclePreview(vehicle: VehicleFB): Task<Void> {
    val fb = FirebaseService.getInstance()
    val db = FirebaseFirestore.getInstance()
    val deferred = CompletableDeferred<Task<Void>>()
    var filteredVehiclesList: List<VehiclePreview>
    val docRef = db.collection(Constants.FB_COLLECTION_USER).document(fb.user!!.uid)
    docRef.get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val existingVehiclesData = document.get("vehicles") as? List<HashMap<String, Any>>
                val existingVehicles: List<VehiclePreview> = mapHashVehiclesToList(existingVehiclesData!!)
                filteredVehiclesList = updateOrAddVehicleById(existingVehicles, vehicle)
                val updateTask = docRef.update("vehicles", filteredVehiclesList)
                updateTask.addOnSuccessListener {
                    saveToLog(
                        LogType.INFO,
                        fb.auth,
                        OperationLog.SET_VEHICLE,
                        Constants.LOG_SET_VEHICLE
                    )
                    deferred.complete(updateTask)
                }
                updateTask.addOnFailureListener { e ->
                    Log.e(Constants.TAG_ERROR, Constants.FB_ERROR_DB_OPERATION, e)
                    deferred.completeExceptionally(e)
                }
            }
        }
        .addOnFailureListener { e ->
            Log.e(Constants.TAG_ERROR, Constants.FB_ERROR_DB_OPERATION, e)
            deferred.completeExceptionally(e)
        }
    return deferred.await()
}


fun updateOrAddVehicleById(existingVehicles: List<VehiclePreview>?, vehicle: VehicleFB): List<VehiclePreview> {
    val filteredVehiclesList = mutableListOf<VehiclePreview>()
    if (existingVehicles == null) {
        return mutableListOf(mapVehicleToVehiclePreview(vehicle))
    }
    val existingVehicleIndex = existingVehicles.indexOfFirst { it.vehicleId == vehicle.vehicleId }
    if (existingVehicleIndex != -1) {
        for ((index, existingVehicle) in existingVehicles.withIndex()) {
            if (index == existingVehicleIndex) {
                val updatedVehicle = mapVehicleToVehiclePreview(vehicle)
                filteredVehiclesList.add(updatedVehicle)
            } else {
                filteredVehiclesList.add(existingVehicle)
            }
        }
    } else {
        filteredVehiclesList.addAll(existingVehicles)
        filteredVehiclesList.add(mapVehicleToVehiclePreview(vehicle))
    }

    return filteredVehiclesList
}


@RequiresApi(Build.VERSION_CODES.O)
fun fbCreateLog(
    type: LogType,
    currentUser: FirebaseUser?,
    uid: String? = "",
    operation: OperationLog,
    content: String = "",
): ItemLog {
    val email = currentUser?.email ?: ""
    val uid = currentUser?.uid ?: ""
    return ItemLog(getTimestamp(), type, operation, email, uid, content)
}

fun fbSaveUserLocally(auth: FirebaseAuth): FirebaseUser? {
    val fb = FirebaseService.getInstance()
    fb.user = auth.currentUser
    fb.auth = auth
    return fb.user
}

fun fbGetUserLogged(): FirebaseUser? {
    val fb = FirebaseService.getInstance()
    return fb.user
}
