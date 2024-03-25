package com.juanmaGutierrez.carcare.service

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import com.juanmaGutierrez.carcare.model.ItemLog
import com.juanmaGutierrez.carcare.service.Constants.Companion.TAG
import java.time.LocalDateTime
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
fun fbSaveLog(itemLog: ItemLog) {
    println(itemLog)
    val db = FirebaseFirestore.getInstance()
    val id = convertDateToID(itemLog.dateTime)
    db.collection("log").document(id)
        .set(itemLog)
        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
}

@RequiresApi(Build.VERSION_CODES.O)
fun convertDateToID(dateTime: LocalDateTime): String {
    val year = dateTime.year
    val month = String.format("%02d", dateTime.monthValue)
    val day = String.format("%02d", dateTime.dayOfMonth)
    val hour = String.format("%02d", dateTime.hour)
    val minute = String.format("%02d", dateTime.minute)
    val second = String.format("%02d", dateTime.second)
    val rand = String.format("%04d", Random.nextInt(10000))
    val id = "$year$month$day$hour$minute$second$rand"
    return id
}



