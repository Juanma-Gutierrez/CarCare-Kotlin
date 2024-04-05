package com.juanmaGutierrez.carcare.service

import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
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