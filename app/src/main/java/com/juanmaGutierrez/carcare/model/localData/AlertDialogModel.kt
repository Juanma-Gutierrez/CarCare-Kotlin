package com.juanmaGutierrez.carcare.model.localData

import android.app.Activity
import android.graphics.drawable.Drawable

/**
 * Represents a model for an alert dialog.
 * @property activity The activity associated with the alert dialog.
 * @property title The title of the alert dialog.
 * @property message The message of the alert dialog.
 * @property icon The icon of the alert dialog.
 */
data class AlertDialogModel(
    val activity: Activity, val title: String, val message: String, val icon: Drawable?
) {
    /**
     * Returns a string representation of the alert dialog model.
     */
    override fun toString(): String {
        return "Activity: $activity\n" + "Title: $title\n" + "Message: $message\n" + "Icon: $icon"
    }
}

/**
 * Represents a model for user interface messages.
 * @property alertDialog The alert dialog messages.
 * @property snackbarMessages The snackbar messages.
 * @property logMessages The log messages.
 */
data class UIUserMessages(
    var alertDialog: AlertDialogMessage = AlertDialogMessage(),
    var snackbarMessages: SnackbarMessages = SnackbarMessages(),
    var logMessages: LogMessages = LogMessages()
) {
    /**
     * Returns a string representation of the UI user messages.
     */
    override fun toString(): String {
        return "Title: ${alertDialog.title}\n" + "Message: ${alertDialog.message}\n" + "CreateOrEditSnackbarMessage: ${snackbarMessages.createOrEditSuccessful}\n" + "DeleteSnackbarMessage: ${snackbarMessages.deleteSuccessful}\n" + "LogContentSuccessMessage: ${logMessages.createOrEditionSuccess}\n" + "LogContentErrorMessage: ${logMessages.createOrEditionError}"
    }
}

/**
 * Represents a model for alert dialog messages.
 * @property title The title of the alert dialog message.
 * @property message The message of the alert dialog message.
 */
data class AlertDialogMessage(
    var title: String = "", var message: String = ""
)

/**
 * Represents a model for snackbar messages.
 * @property createOrEditSuccessful The successful message for create or edit actions.
 * @property createOrEditError The error message for create or edit actions.
 * @property deleteSuccessful The successful message for delete actions.
 * @property deletionError The error message for delete actions.
 */
data class SnackbarMessages(
    var createOrEditSuccessful: String = "",
    var createOrEditError: String = "",
    var deleteSuccessful: String = "",
    var deletionError: String = "",
)

/**
 * Represents a model for log messages.
 * @property createOrEditionSuccess The success message for create or edit actions in logs.
 * @property createOrEditionError The error message for create or edit actions in logs.
 * @property deleteSuccess The success message for delete actions in logs.
 * @property deleteError The error message for delete actions in logs.
 */
data class LogMessages(
    var createOrEditionSuccess: String = "",
    var createOrEditionError: String = "",
    var deleteSuccess: String = "",
    var deleteError: String = ""
)