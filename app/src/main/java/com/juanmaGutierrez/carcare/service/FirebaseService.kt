package com.juanmaGutierrez.carcare.service

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.juanmaGutierrez.carcare.mapping.mapHashVehiclesToList
import com.juanmaGutierrez.carcare.mapping.mapUserToUserFB
import com.juanmaGutierrez.carcare.mapping.mapVehicleToVehiclePreview
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.Constants.Companion.ERROR_CREATE_USER_WITH_EMAIL
import com.juanmaGutierrez.carcare.model.Constants.Companion.ERROR_DATABASE
import com.juanmaGutierrez.carcare.model.Constants.Companion.TAG
import com.juanmaGutierrez.carcare.model.firebase.VehicleFB
import com.juanmaGutierrez.carcare.model.firebase.VehicleImagePackToFB
import com.juanmaGutierrez.carcare.model.localData.ItemLog
import com.juanmaGutierrez.carcare.model.localData.LogType
import com.juanmaGutierrez.carcare.model.localData.OperationLog
import com.juanmaGutierrez.carcare.model.localData.Providers
import com.juanmaGutierrez.carcare.model.localData.User
import com.juanmaGutierrez.carcare.model.localData.VehiclePreview
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
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

fun fbRegisterUserAuth(user: User, callback: (Boolean) -> Unit) {
    val auth = Firebase.auth
    auth.createUserWithEmailAndPassword(user.email, user.password)
        .addOnCompleteListener(Executors.newSingleThreadExecutor()) { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser?.uid ?: ""
                fbCreateUser(user, uid)
                fbCreateProviders(uid)
                saveToLog(LogType.INFO, OperationLog.CREATE_USER, Constants.REGISTER_SUCCESSFULLY)
                callback.invoke(true)
            } else {
                Log.e(TAG, ERROR_CREATE_USER_WITH_EMAIL, task.exception)
                callback.invoke(false)
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

fun fbGetDocumentByID(itemID: String, collection: String, callback: (DocumentSnapshot?) -> Unit) {
    val db = Firebase.firestore
    try {
        val docRef = db.collection(collection).document(itemID)
        docRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                callback(document)
            } else {
                Log.e(Constants.TAG_ERROR, Constants.FB_NO_DOCUMENT)
                callback(null)
            }
        }.addOnFailureListener { e ->
            Log.e(Constants.TAG_ERROR, Constants.ERROR_EXCEPTION_PREFIX, e)
            callback(null)
        }
    } catch (e: Exception) {
        Log.e(Constants.TAG_ERROR, Constants.ERROR_FIREBASE_CALL, e)
    }
}

fun fbSetDocument(collection: String, documentId: String, document: Any): Task<Void> {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection(collection).document(documentId)
    return docRef.set(document)
        .addOnFailureListener { e -> Log.e(Constants.TAG_ERROR, Constants.FB_ERROR_DB_OPERATION, e) }
}

suspend fun fbSetVehiclePreview(vehicle: VehicleFB): Task<Void> {
    val fb = FirebaseService.getInstance()
    val db = FirebaseFirestore.getInstance()
    val deferred = CompletableDeferred<Task<Void>>()
    var filteredVehiclesList: List<VehiclePreview>
    val docRef = db.collection(Constants.FB_COLLECTION_USER).document(fb.user!!.uid)
    docRef.get().addOnSuccessListener { document ->
        if (document.exists()) {
            val existingVehiclesData = document.get("vehicles") as? List<HashMap<String, Any>>
            val existingVehicles: List<VehiclePreview> = mapHashVehiclesToList(existingVehiclesData!!)
            filteredVehiclesList = updateOrAddVehicleById(existingVehicles, vehicle)
            val updateTask = docRef.update("vehicles", filteredVehiclesList)
            updateTask.addOnSuccessListener { deferred.complete(updateTask) }
            updateTask.addOnFailureListener { e ->
                Log.e(Constants.TAG_ERROR, Constants.FB_ERROR_DB_OPERATION, e)
                deferred.completeExceptionally(e)
            }
        }
    }.addOnFailureListener { e ->
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

suspend fun fbDeleteDocumentByID(collection: String, id: String) {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection(collection).document(id)
    try {
        docRef.delete().await()
    } catch (e: Exception) {
        Log.e(Constants.TAG_ERROR, Constants.FB_ERROR_DB_OPERATION, e)
    }
}

suspend fun fbDeleteVehiclePreview(vehicle: VehicleFB) {
    val db = FirebaseFirestore.getInstance()
    try {
        val docRef = db.collection(Constants.FB_COLLECTION_USER).document(vehicle.userId).get().await()
        if (docRef.exists()) {
            val existingVehiclesData = docRef.get("vehicles") as? List<HashMap<String, Any>>
            val existingVehicles: List<VehiclePreview> = mapHashVehiclesToList(existingVehiclesData!!)
            val filtered = existingVehicles.filter { it.vehicleId != vehicle.vehicleId }
            db.collection(Constants.FB_COLLECTION_USER).document(vehicle.userId).update("vehicles", filtered).await()
        } else {
            Log.e(Constants.TAG_ERROR, Constants.LOG_VEHICLE_DELETION_ERROR)
        }
    } catch (e: Exception) {
        Log.e(Constants.TAG_ERROR, Constants.LOG_VEHICLE_DELETION_ERROR, e)
    }
}

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

fun fbSaveImage(imagePack: VehicleImagePackToFB): String {
    val storageRef = Firebase.storage.reference
    val userID = Firebase.auth.uid
    val formattedURL = "vehicleImages/$userID/${imagePack.name}.jpg"
    val imageRef = storageRef.child(formattedURL)
    val inputStream = imagePack.context.contentResolver.openInputStream(imagePack.uri)
    val bitmap = BitmapFactory.decodeStream(inputStream)
    val bitmapResized = resizeBitmap(bitmap)
    val compressedData = compressBitmap(bitmapResized, 50)
    val uploadTask = imageRef.putBytes(compressedData)
    uploadTask.addOnSuccessListener { taskSnapshot ->
        Log.i(TAG, "Image saved to Firestore storage: ${taskSnapshot.metadata?.name}")
    }.addOnFailureListener { exception ->
        Log.e(Constants.TAG_ERROR, Constants.ERROR_FIREBASE_CALL, exception)
    }
    return formattedURL
}

private fun resizeBitmap(bitmap: Bitmap): Bitmap {
    val newWidth = 500
    val width = bitmap.width
    val height = bitmap.height
    val aspectRatio = width.toFloat() / height
    val newHeight = (newWidth / aspectRatio).toInt()
    val matrix = Matrix()
    matrix.postScale(newWidth.toFloat() / width, newHeight.toFloat() / height)
    return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)
}

fun compressBitmap(bitmap: Bitmap, quality: Int): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
    return stream.toByteArray()
}

fun fbGetImageURL(imageURL: String, callback: (String?) -> Unit) {
    if (imageURL.isNotEmpty()) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val pathReference = storageRef.child(imageURL)
        storageRef.child(pathReference.path).downloadUrl.addOnSuccessListener { url ->
            callback(url.toString())
        }.addOnFailureListener {
            callback(null)
        }
    } else {
        callback(null)
    }
}

fun fbGetAuthUserUID(): String {
    val user = FirebaseService.getInstance()
    return user.auth?.uid.toString()
}