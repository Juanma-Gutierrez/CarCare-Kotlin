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
import com.juanmaGutierrez.carcare.mapping.mapHashVehiclesToListVehiclePreview
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

/**
 * Provides Firebase authentication and user information.
 */
class FirebaseService {
    var user: FirebaseUser? = null
    var auth: FirebaseAuth? = null

    /**
     * Returns a string representation of FirebaseService instance.
     *
     * @return A string representation of FirebaseService.
     */
    override fun toString(): String {
        var data = ""
        if (user != null) {
            data = "-FirebaseService-\nUSER ID: ${user!!.uid}\nUSER EMAIL: ${user!!.email}\n"
        }
        return data
    }

    /**
     * Gets the singleton instance of FirebaseService.
     *
     * @return The singleton instance of FirebaseService.
     */
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

/**
 * Saves a log item to Firestore.
 *
 * @param itemLog The log item to save.
 */
fun fbSaveLog(itemLog: ItemLog) {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection(Constants.FB_COLLECTION_LOG).document(Constants.COLLECTION_LOG_DOC)
    docRef.update(Constants.COLLECTION_LOG_ARRAYLIST, FieldValue.arrayUnion(itemLog)).addOnSuccessListener {}
        .addOnFailureListener { e ->
            val logMap = mapOf("logs" to listOf(itemLog))
            docRef.set(logMap)
            Log.e(TAG, ERROR_DATABASE, e)
        }
}

/**
 * Registers a user with email and password authentication in Firebase.
 *
 * @param user The user object containing email and password.
 * @param callback The callback function to invoke after registration is complete.
 */
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

/**
 * Creates user data in Firestore.
 *
 * @param user The user object containing user data.
 * @param uid The user ID.
 */
fun fbCreateUser(user: User, uid: String) {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection(Constants.FB_COLLECTION_USER).document(uid)
    val mappedUser = mapUserToUserFB(user, uid)
    docRef.set(mappedUser).addOnSuccessListener {}
        .addOnFailureListener { e -> Log.e(Constants.TAG_ERROR, Constants.FB_ERROR_DB_OPERATION, e) }
}

/**
 * Creates provider data in Firestore.
 *
 * @param uid The user ID.
 */
fun fbCreateProviders(uid: String) {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection(Constants.FB_COLLECTION_PROVIDER).document(uid)
    val providers = Providers(emptyList())
    docRef.set(providers).addOnSuccessListener {}
        .addOnFailureListener { e -> Log.e(Constants.TAG_ERROR, Constants.FB_ERROR_DB_OPERATION, e) }
}

/**
 * Retrieves a Firestore document by ID.
 *
 * @param itemId The ID of the document to retrieve.
 * @param collection The collection containing the document.
 * @param callback The callback function to invoke with the document snapshot.
 */
fun fbGetDocumentByID(itemId: String, collection: String, callback: (DocumentSnapshot?) -> Unit) {
    val db = Firebase.firestore
    try {
        val docRef = db.collection(collection).document(itemId)
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

/**
 * Sets a document in Firestore.
 *
 * @param collection The collection where the document is stored.
 * @param documentId The ID of the document.
 * @param document The document to set.
 * @return A task representing the set operation.
 */
fun fbSetDocument(collection: String, documentId: String, document: Any): Task<Void> {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection(collection).document(documentId)
    return docRef.set(document)
        .addOnFailureListener { e -> Log.e(Constants.TAG_ERROR, Constants.FB_ERROR_DB_OPERATION, e) }
}

/**
 * Sets the vehicle preview data in Firestore.
 *
 * @param vehicle The vehicle data to set.
 * @return A task representing the set operation.
 */
suspend fun fbSetVehiclePreview(vehicle: VehicleFB): Task<Void> {
    val fb = FirebaseService.getInstance()
    val db = FirebaseFirestore.getInstance()
    val deferred = CompletableDeferred<Task<Void>>()
    var filteredVehiclesList: List<VehiclePreview>
    val docRef = db.collection(Constants.FB_COLLECTION_USER).document(fb.user!!.uid)
    docRef.get().addOnSuccessListener { document ->
        if (document.exists()) {
            val existingVehiclesData = document.get("vehicles") as? List<HashMap<String, Any>>
            val existingVehicles: List<VehiclePreview> = mapHashVehiclesToListVehiclePreview(existingVehiclesData!!)
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

/**
 * Updates or adds a vehicle to the existing list based on its ID.
 *
 * @param existingVehicles The existing list of vehicles.
 * @param vehicle The vehicle to update or add.
 * @return The updated list of vehicles.
 */
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

/**
 * Deletes a Firestore document by ID.
 *
 * @param collection The collection where the document is stored.
 * @param id The ID of the document to delete.
 */
suspend fun fbDeleteDocumentByID(collection: String, id: String) {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection(collection).document(id)
    try {
        docRef.delete().await()
    } catch (e: Exception) {
        Log.e(Constants.TAG_ERROR, Constants.FB_ERROR_DB_OPERATION, e)
    }
}

/**
 * Deletes a vehicle preview from Firestore.
 *
 * @param vehicle The vehicle to delete.
 */
suspend fun fbDeleteVehiclePreview(vehicle: VehicleFB) {
    val db = FirebaseFirestore.getInstance()
    try {
        val docRef = db.collection(Constants.FB_COLLECTION_USER).document(vehicle.userId).get().await()
        if (docRef.exists()) {
            val existingVehiclesData = docRef.get("vehicles") as? List<HashMap<String, Any>>
            val existingVehicles: List<VehiclePreview> = mapHashVehiclesToListVehiclePreview(existingVehiclesData!!)
            val filtered = existingVehicles.filter { it.vehicleId != vehicle.vehicleId }
            db.collection(Constants.FB_COLLECTION_USER).document(vehicle.userId).update("vehicles", filtered).await()
        } else {
            Log.e(Constants.TAG_ERROR, Constants.LOG_VEHICLE_DELETION_ERROR)
        }
    } catch (e: Exception) {
        Log.e(Constants.TAG_ERROR, Constants.LOG_VEHICLE_DELETION_ERROR, e)
    }
}

/**
 * Creates a log item for Firebase.
 *
 * @param type The type of the log.
 * @param currentUser The current Firebase user.
 * @param uid The user ID.
 * @param operation The operation log.
 * @param content The content of the log.
 * @return An ItemLog object.
 */
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

/**
 * Saves the currently authenticated user locally in FirebaseService.
 *
 * @param auth The FirebaseAuth instance.
 * @return The currently authenticated Firebase user.
 */
fun fbSaveUserLocally(auth: FirebaseAuth): FirebaseUser? {
    val fb = FirebaseService.getInstance()
    fb.user = auth.currentUser
    fb.auth = auth
    return fb.user
}

/**
 * Retrieves the currently logged-in Firebase user.
 *
 * @return The currently logged-in Firebase user.
 */
fun fbGetUserLogged(): FirebaseUser? {
    val fb = FirebaseService.getInstance()
    return fb.user
}

/**
 * Saves an image to Firestore storage.
 *
 * @param imagePack The image data to save.
 * @return The URL of the saved image.
 */
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

/**
 * Resizes a bitmap to a specified width.
 *
 * @param bitmap The bitmap to resize.
 * @return The resized bitmap.
 */
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

/**
 * Compresses a bitmap.
 *
 * @param bitmap The bitmap to compress.
 * @param quality The quality of compression.
 * @return The compressed bitmap as a byte array.
 */
fun compressBitmap(bitmap: Bitmap, quality: Int): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
    return stream.toByteArray()
}

/**
 * Retrieves the download URL of an image stored in Firestore storage.
 *
 * @param imageURL The URL of the image.
 * @param callback The callback function to handle the retrieved URL.
 */
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

/**
 * Retrieves the UID of the currently authenticated user.
 *
 * @return The UID of the currently authenticated user.
 */
fun fbGetAuthUserUID(): String {
    val user = FirebaseService.getInstance()
    return user.auth?.uid.toString()
}