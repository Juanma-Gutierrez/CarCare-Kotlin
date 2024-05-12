package com.juanmaGutierrez.carcare.model.localData

import android.app.Activity
import android.graphics.drawable.Drawable

data class AlertDialogModel(
    val activity: Activity, val title: String, val message: String, val icon: Drawable?
) {
    override fun toString(): String {
        return "Activity: $activity\n" + "Title: $title\n" + "Message: $message\n" + "Icon: $icon"
    }
}

data class UIUserMessages(
    var alertDialog: AlertDialogMessage = AlertDialogMessage(),
    var snackbarMessages: SnackbarMessages = SnackbarMessages(),
    var logMessages: LogMessages = LogMessages()
) {
    override fun toString(): String {
        return "Title: ${alertDialog.title}\n" + "Message: ${alertDialog.message}\n" + "CreateOrEditSnackbarMessage: ${snackbarMessages.createOrEditSuccessful}\n" + "DeleteSnackbarMessage: ${snackbarMessages.deleteSuccessful}\n" + "LogContentSuccessMessage: ${logMessages.createOrEditionSuccess}\n" + "LogContentErrorMessage: ${logMessages.createOrEditionError}"
    }
}

data class AlertDialogMessage(
    var title: String = "", var message: String = ""
)

data class SnackbarMessages(
    var createOrEditSuccessful: String = "",
    var createOrEditError: String = "",
    var deleteSuccessful: String = "",
    var deletionError: String = "",
)

data class LogMessages(
    var createOrEditionSuccess: String = "",
    var createOrEditionError: String = "",
    var deleteSuccess: String = "",
    var deleteError: String = ""
)