package com.juanmaGutierrez.carcare.model.localData

import android.app.Activity
import android.graphics.drawable.Drawable

data class AlertDialogModel(
    val activity: Activity, val title: String, val message: String, val icon: Drawable?
) {
    override fun toString(): String {
        return "Activity: $activity\nTitle: $title\nMessage: $message\nIcon: $icon"
    }
}

data class AlertDialogMessageModel(
    var title: String? = null,
    var message: String? = null,
    var logContentSuccessMessage: String? = null,
    var logContentErrorMessage: String? = null,
) {
    override fun toString(): String {
        return "Title: $title\nMessage: $message\nLogContentSuccessMessage: $logContentSuccessMessage\nLogContentErrorMessage: $logContentErrorMessage"
    }
}
