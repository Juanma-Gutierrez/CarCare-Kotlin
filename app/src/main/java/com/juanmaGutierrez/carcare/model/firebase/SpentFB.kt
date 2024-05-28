package com.juanmaGutierrez.carcare.model.firebase

import android.os.Build
import androidx.annotation.RequiresApi
import com.juanmaGutierrez.carcare.service.euroFormat
import com.juanmaGutierrez.carcare.service.transformDateIsoToString
import kotlin.math.min

data class SpentFB(
    val amount: Double,
    val created: String,
    val date: String,
    val observations: String,
    val providerId: String,
    val providerName: String,
    val spentId: String
) {
    override fun toString(): String {
        return "Amount: $amount\nCreated: $created\nDate: $date\nObservations: $observations\nProviderId: $providerId\nProviderName: $providerName\nSpentId: $spentId"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toExport(): String {
        val dateFormatted = date.transformDateIsoToString().substring(0, minOf(date.length, 10))
        val providerFormatted = providerName.substring(0, minOf(providerName.length, 18))
        val amountFormatted = amount.euroFormat()
        return String.format("%-10s %-18s %10s", dateFormatted, providerFormatted, amountFormatted)
    }
}