package com.juanmaGutierrez.carcare.model.localData

import android.app.Activity
import android.graphics.drawable.Drawable

data class AlertDialogModel(
    val activity: Activity, val title: String, val message: String, val icon: Drawable?
) {
    override fun toString(): String {
        return "Activity: $activity\n" +
                "Title: $title\n" +
                "Message: $message\n" +
                "Icon: $icon"
    }
}

data class UIUserMessages(
    var alertDialog: AlertDialogMessage = AlertDialogMessage(),
    var snackbarMessages: SnackbarMessages = SnackbarMessages(),
    var logMessages: LogMessages = LogMessages()
    /*    var title: String = "",
        var message: String = "",
        var createSnackbarMessage: String = "",
        var editSnackbarMessage: String = "",
        var deleteSnackbarMessage: String = "",
        var logContentSuccessMessage: String = "",
        var logContentErrorMessage: String = "",*/
) {
    override fun toString(): String {
        return "Title: ${alertDialog.title}\n" +
                "Message: ${alertDialog.message}\n" +
                "CreateOrEditSnackbarMessage: ${snackbarMessages.createOrEditSuccessful}\n" +
                "DeleteSnackbarMessage: ${snackbarMessages.deleteSuccessful}\n" +
                "LogContentSuccessMessage: ${logMessages.success}\n" +
                "LogContentErrorMessage: ${logMessages.error}"
    }
}

data class AlertDialogMessage(
    var title: String = "",
    var message: String = ""
)

data class SnackbarMessages(
    var createOrEditSuccessful: String = "",
    var createOrEditError: String = "",
    var deleteSuccessful: String = "",
    var deletionError: String = "",
)

data class LogMessages(
    var success: String = "",
    var error: String = ""
)