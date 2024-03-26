package com.juanmaGutierrez.carcare.service

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.juanmaGutierrez.carcare.model.ItemLog
import com.juanmaGutierrez.carcare.model.Providers
import com.juanmaGutierrez.carcare.model.User
import com.juanmaGutierrez.carcare.model.UserFB
import com.juanmaGutierrez.carcare.service.Constants.Companion.TAG
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

@RequiresApi(Build.VERSION_CODES.O)
fun fbSaveLog(itemLog: ItemLog) {
    println(itemLog)
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection(Constants.COL_LOG).document(Constants.COL_LOG_DOC)
    docRef.update(Constants.COL_LOG_ARRAYLIST, FieldValue.arrayUnion(itemLog))
        .addOnSuccessListener {}
        .addOnFailureListener { e ->
            val logMap = mapOf("logs" to listOf(itemLog))
            docRef.set(logMap)
            Log.e(TAG, "Error in database operation", e)
        }
}

fun fbRegisterUserAuth(user: User) {
    val auth = Firebase.auth
    auth.createUserWithEmailAndPassword(user.email, user.password)
        .addOnCompleteListener(Executors.newSingleThreadExecutor()) { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser?.uid ?: ""
                fbCreateUser(user, uid)
                fbCreateProviders(uid)
            } else {
                Log.e(TAG, "createUserWithEmail:failure", task.exception)
            }
        }
}

fun fbCreateUser(user: User, uid: String) {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("user").document(uid)
    val mappedUser = mapUser(user, uid)
    docRef.set(mappedUser)
        .addOnSuccessListener {}
        .addOnFailureListener { e -> Log.e("ERROR", "Error in database operation", e) }
}

fun fbCreateProviders(uid: String) {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("provider").document(uid)
    val providers = Providers(emptyList())
    docRef.set(providers)
        .addOnSuccessListener {}
        .addOnFailureListener { e -> Log.e("ERROR", "Error in database operation", e) }
}

fun getTimestamp(): String {
    val currentTimestamp = Date()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val timestampString = dateFormat.format(currentTimestamp)
    return timestampString
}

fun mapUser(user: User, uid: String): UserFB {
    val currentTimeStamp = getTimestamp()
    val data = UserFB(
        currentTimeStamp,
        user.email,
        user.name,
        user.username,
        "user",
        user.surname,
        uid,
        emptyList(),
    )
    return data
}

