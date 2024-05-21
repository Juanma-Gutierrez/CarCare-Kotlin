package com.juanmaGutierrez.carcare.service

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.annotation.RequiresApi
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

fun showDialogAcceptCancel(ad: AlertDialogModel, callback: (Boolean) -> Unit) {
    MaterialAlertDialogBuilder(ad.activity).setTitle(ad.title).setMessage(ad.message).setIcon(ad.icon)
        .setPositiveButton(ad.activity.getString(R.string.accept)) { _, _ ->
            callback(true)
        }.setNegativeButton(ad.activity.getString(R.string.cancel)) { _, _ ->
            callback(false)
        }.show()
}

fun showDialogAccept(ad: AlertDialogModel, callback: (Boolean) -> Unit) {
    MaterialAlertDialogBuilder(ad.activity).setTitle(ad.title).setMessage(ad.message).setIcon(ad.icon)
        .setPositiveButton(ad.activity.getString(R.string.accept)) { _, _ ->
            callback(true)
        }.show()
}


fun loadDataInSelectable(selectable: AutoCompleteTextView, listItems: List<String>, activity: Activity) {
    val adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, listItems.sorted())
    selectable.setAdapter(adapter)
}

@RequiresApi(Build.VERSION_CODES.O)
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

fun getTimestamp(): String {
    val currentTimestamp = Date()
    val dateFormat = SimpleDateFormat(Constants.DATE_FORMAT_ISO, Locale.getDefault())
    return dateFormat.format(currentTimestamp)
}

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

fun milog(string: String, t: Throwable? = null) {
    Log.d("jumang", string, t)
}

@RequiresApi(Build.VERSION_CODES.O)
fun generateId(): String {
    val formattedDate = getTimestamp().transformDateIsoToString("yyMMddHHmmss-")
    val length = 10
    val allowedChars = ('a'..'z') + ('0'..'9')
    return formattedDate + (1..length).map { allowedChars.random() }.joinToString("")
}

fun String.toUpperCamelCase(delimiter: String = " "): String {
    return split(delimiter).joinToString(delimiter) { word ->
        word.lowercase().replaceFirstChar(Char::uppercase)
    }
}

fun Double.euroFormat(): String {
    return String.format("%.2f €", this)
}

fun Double.moneyInputFormat(): String {
    return String.format("%.2f", this)
}

fun String.toCapitalizeString(): String {
    return if (isNotEmpty()) {
        this[0].uppercaseChar() + substring(1).lowercase()
    } else {
        this
    }
}


fun String.convertDateMillisToDate(): String {
    val timestamp = this.toLong()
    val date = Date(timestamp)
    val dateFormat = SimpleDateFormat(Constants.DATE_FORMAT_ISO, Locale.getDefault())
    return dateFormat.format(date)
}

@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
fun String.longToTimestamp(): Long {
    val formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_LOCAL)
    val localDate = LocalDate.parse(this, formatter)
    return localDate.toEpochDay() * 24 * 60 * 60 * 1000
}

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

fun String.getVehicleCategoryTranslation(context: Context): String {
    return when (this) {
        "car" -> context.getString(R.string.vehicle_category_car)
        "motorcycle" -> context.getString(R.string.vehicle_category_motorcycle)
        "van" -> context.getString(R.string.vehicle_category_van)
        "truck" -> context.getString(R.string.vehicle_category_truck)
        else -> this
    }
}

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
