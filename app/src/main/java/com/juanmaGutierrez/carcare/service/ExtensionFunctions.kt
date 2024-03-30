package com.juanmaGutierrez.carcare.service

import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar

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

fun toUpperCamelCase(input: String): String {
    return input.split(" ").joinToString("") { it.replaceFirstChar { char -> char.uppercase() } }
}