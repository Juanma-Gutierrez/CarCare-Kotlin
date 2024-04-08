package com.juanmaGutierrez.carcare.service

import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.juanmaGutierrez.carcare.model.localData.LogType
import com.juanmaGutierrez.carcare.model.localData.OperationLog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/**
 * Usage: showSnackBar("Message", requireView())
 * Usage: showSnackBar("Message", findViewById(android.R.id.content))
 */
fun showSnackBar(message: String, view: View) {
    val snackBar = Snackbar.make(
        view,
        message,
        Snackbar.LENGTH_SHORT
    )
    val snackBarView = snackBar.view
    val layoutParams = snackBarView.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(
        layoutParams.leftMargin,
        layoutParams.topMargin,
        layoutParams.rightMargin,
        250
    )
    snackBarView.layoutParams = layoutParams
    snackBar.show()
}

fun String.toUpperCamelCase(delimiter: String = " "): String {
    return split(delimiter).joinToString(delimiter) { word ->
        word.lowercase().replaceFirstChar(Char::uppercase)
    }
}

fun getTimestamp(): String {
    val currentTimestamp = Date()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    return dateFormat.format(currentTimestamp)
}

@RequiresApi(Build.VERSION_CODES.O)
fun saveToLog(
    type: LogType,
    auth: FirebaseAuth,
    operation: OperationLog,
    content: String,
    onComplete: (() -> Unit)? = null
) {
    fbSaveLog(
        fbCreateLog(
            type,
            auth.currentUser!!,
            auth.currentUser?.uid,
            operation,
            content
        )
    )
    onComplete?.invoke()
}

fun log(string: String, t: Throwable? = null) {
    Log.d("wanma", string, t)
}