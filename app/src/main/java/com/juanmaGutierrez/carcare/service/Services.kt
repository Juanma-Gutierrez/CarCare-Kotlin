package com.juanmaGutierrez.carcare.service

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar

class Services {}

/**
 * Usage: showSnackBar("Message", requireView())
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

/*
fun Activity.getView(action: (View) -> Unit) {
    System.out.println("call getView")
    val contentView = findViewById<View>(android.R.id.content)
    action(contentView)
}
*/

