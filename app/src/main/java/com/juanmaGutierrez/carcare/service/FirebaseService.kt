package com.juanmaGutierrez.carcare.service

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.juanmaGutierrez.carcare.model.ItemLog

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
            Log.e("wanma", "Error in database operation", e)
        }
}




