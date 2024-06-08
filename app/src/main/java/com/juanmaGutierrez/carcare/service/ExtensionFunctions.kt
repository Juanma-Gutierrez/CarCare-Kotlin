package com.juanmaGutierrez.carcare.service

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.localData.AlertDialogModel
import com.juanmaGutierrez.carcare.model.localData.LogType
import com.juanmaGutierrez.carcare.model.localData.OperationLog
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale

/**
 * Displays a Snackbar with the given message.
 *
 * @param message The message to be displayed in the Snackbar.
 * @param view The view to anchor the Snackbar to.
 * @param onDismiss A function to be called after the Snackbar is dismissed.
 * Usage: showSnackBar("Message", requireView()) { <function-after-snackbar> }
 * Usage: showSnackBar("Message", findViewById(android.R.id.content)) { <function-after-snackbar> }
 * Usage: view?.let { showSnackBar("Message", it) {} }
 */
fun showSnackBar(message: String, view: View, onDismiss: () -> Unit) {
    val timeToShow = 1000
    val snackBar = Snackbar.make(view, message, timeToShow)
    val snackBarView = snackBar.view
    val layoutParams = snackBarView.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, 250)
    snackBarView.layoutParams = layoutParams
    snackBar.addCallback(object : Snackbar.Callback() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            super.onDismissed(transientBottomBar, event)
            Handler(Looper.getMainLooper()).postDelayed({ onDismiss() }, timeToShow.toLong())
        }
    })
    snackBar.show()
}

/**
 * Displays an AlertDialog with Accept and Cancel buttons.
 *
 * @param ad The AlertDialogModel containing title, message, and icon.
 * @param callback A function to be called after the user interacts with the dialog.
 */
fun showDialogAcceptCancel(ad: AlertDialogModel, callback: (Boolean) -> Unit) {
    MaterialAlertDialogBuilder(ad.activity).setTitle(ad.title).setMessage(ad.message).setIcon(ad.icon)
        .setPositiveButton(ad.activity.getString(R.string.accept)) { _, _ ->
            callback(true)
        }.setNegativeButton(ad.activity.getString(R.string.cancel)) { _, _ ->
            callback(false)
        }.show()
}

/**
 * Loads data into an AutoCompleteTextView.
 *
 * @param selectable The AutoCompleteTextView to load data into.
 * @param listItems The list of items to be displayed.
 * @param activity The activity context.
 */
fun loadDataInSelectable(selectable: AutoCompleteTextView, listItems: List<String>, activity: Activity) {
    val adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, listItems.sorted())
    selectable.setAdapter(adapter)
}

/**
 * Displays a DatePickerDialog.
 *
 * @param initialDate The initial date to be displayed.
 * @param title The title of the dialog.
 * @param fragmentManager The FragmentManager to control the dialog.
 * @param onDateSelected A function to be called when a date is selected.
 */
fun showDatePickerDialog(
    initialDate: String, title: String, fragmentManager: FragmentManager, onDateSelected: (String) -> Unit
) {
    val dateFormat = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_LOCAL)
    val builder =
        MaterialDatePicker.Builder.datePicker().setTitleText(title).setSelection(initialDate.longToTimestamp())
    val datePicker = builder.build()
    datePicker.addOnPositiveButtonClickListener { selectedDate ->
        val formattedDate = LocalDate.ofEpochDay(selectedDate / 86400000).format(dateFormat)
        onDateSelected(formattedDate)
    }
    datePicker.show(fragmentManager, "datePickerDialog")
}

/**
 * Gets the current timestamp in ISO format.
 *
 * @return The current timestamp.
 */
fun getTimestamp(): String {
    val currentTimestamp = Date()
    val dateFormat = SimpleDateFormat(Constants.DATE_FORMAT_ISO, Locale.getDefault())
    return dateFormat.format(currentTimestamp)
}

/**
 * Saves a log entry.
 *
 * @param type The type of log (INFO or ERROR).
 * @param operation The operation performed.
 * @param content The content of the log entry.
 * @param onComplete A function to be called after the log is saved.
 */
fun saveToLog(
    type: LogType, operation: OperationLog, content: String, onComplete: (() -> Unit)? = null
) {
    val auth = FirebaseAuth.getInstance()
    fbSaveLog(
        fbCreateLog(
            type, auth.currentUser!!, auth.currentUser!!.uid, operation, content
        )
    )
    onComplete?.invoke()
}

/**
 * Generates a unique identifier using the current timestamp and random characters.
 *
 * @return A unique identifier.
 */
fun generateId(): String {
    val formattedDate = getTimestamp().transformDateIsoToString("yyMMddHHmmss-")
    val length = 10
    val allowedChars = ('a'..'z') + ('0'..'9')
    return formattedDate + (1..length).map { allowedChars.random() }.joinToString("")
}

/**
 * Converts a string to Upper Camel Case format.
 *
 * @param delimiter The delimiter to split the string.
 * @return The string in Upper Camel Case format.
 */
fun String.toUpperCamelCase(delimiter: String = " "): String {
    return split(delimiter).joinToString(delimiter) { word ->
        word.lowercase().replaceFirstChar(Char::uppercase)
    }
}

/**
 * Formats a double value to Euro currency format.
 *
 * @return The double value formatted as Euro currency.
 */
fun Double.euroFormat(): String {
    return String.format("%.2f €", this)
}

/**
 * Formats a double value to money input format.
 *
 * @return The double value formatted as money input.
 */
fun Double.moneyInputFormat(): String {
    return String.format("%.2f", this)
}

/**
 * Capitalizes the first character of a string.
 *
 * @return The string with the first character capitalized.
 */
fun String.toCapitalizeString(): String {
    return if (isNotEmpty()) {
        this[0].uppercaseChar() + substring(1).lowercase()
    } else {
        this
    }
}

/**
 * Transforms a date string in ISO format to the specified format.
 *
 * @param format The format to transform the date string to.
 * @return The date string transformed to the specified format.
 */
fun String.transformDateIsoToString(format: String = "dd/MM/yyyy"): String {
    return try {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val parsedDate = LocalDateTime.parse(this, inputFormat)
        val outputFormat = DateTimeFormatter.ofPattern(format)
        return parsedDate.format(outputFormat)
    } catch (e: DateTimeParseException) {
        ""
    }
}

/**
 * Transforms a string in a specific format to ISO date format.
 *
 * @return The string transformed to ISO date format.
 */
fun String.transformStringToDateIso(): String {
    return try {
        val inputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss")
        val parsedDate = LocalDateTime.parse("${this}T00:00:00", inputFormat)
        val outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        return parsedDate.format(outputFormat)
    } catch (e: DateTimeParseException) {
        ""
    }
}

/**
 * Converts a string date to a timestamp in milliseconds.
 *
 * @return The timestamp in milliseconds.
 */
fun String.longToTimestamp(): Long {
    val formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_LOCAL)
    val localDate = LocalDate.parse(this, formatter)
    return localDate.toEpochDay() * 24 * 60 * 60 * 1000
}

/**
 * Translates a vehicle category string to a standardized format.
 *
 * @return The translated vehicle category.
 */
fun String.translateVehicleCategory(): String {
    val result = when (this) {
        "Coche", "Car" -> "car"
        "Motocicleta", "Motorcycle" -> "motorcycle"
        "Furgoneta", "Van" -> "van"
        "Camión", "Truck" -> "truck"
        else -> ""
    }
    return result
}

/**
 * Gets the translated vehicle category.
 *
 * @param context The context to access resources.
 * @return The translated vehicle category.
 */
fun String.getVehicleCategoryTranslation(context: Context): String {
    return when (this) {
        "car" -> context.getString(R.string.vehicle_category_car)
        "motorcycle" -> context.getString(R.string.vehicle_category_motorcycle)
        "van" -> context.getString(R.string.vehicle_category_van)
        "truck" -> context.getString(R.string.vehicle_category_truck)
        else -> this
    }
}

/**
 * Translates a provider category string to a standardized format.
 *
 * @return The translated provider category.
 */
fun String.translateProviderCategory(): String {
    val result = when (this) {
        "Taller", "Workshop" -> "workshop"
        "Gasolinera", "Gas station" -> "gasStation"
        "Compañía de seguros", "Insurance company" -> "insuranceCompany"
        "ITV" -> "ITV"
        "Grúa", "Tow truck" -> "towTruck"
        "Otros", "Other" -> "other"
        else -> ""
    }
    return result
}

/**
 * Gets the translated provider category.
 *
 * @param context The context to access resources.
 * @return The translated provider category.
 */
fun String.getProviderCategoryTranslation(context: Context): String {
    return when (this) {
        "workshop" -> context.getString(R.string.provider_category_workshop)
        "gasStation" -> context.getString(R.string.provider_category_gasStation)
        "insuranceCompany" -> context.getString(R.string.provider_category_insuranceCompany)
        "ITV" -> context.getString(R.string.provider_category_ITV)
        "towTruck" -> context.getString(R.string.provider_category_towTruck)
        "other" -> context.getString(R.string.provider_category_other)
        else -> this
    }
}

/**
 * Logs a message with optional throwable.
 *
 * @param string The message to log.
 * @param t The optional throwable.
 */
fun milog(string: String, t: Throwable? = null) {
    Log.d("jumang", string, t)
}