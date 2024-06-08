package com.juanmaGutierrez.carcare.model.firebase

import com.juanmaGutierrez.carcare.service.euroFormat
import com.juanmaGutierrez.carcare.service.toUpperCamelCase
import com.juanmaGutierrez.carcare.service.transformDateIsoToString

/**
 * Represents a spent transaction in Firebase.
 * @property amount The amount spent.
 * @property created The creation date of the spent transaction.
 * @property date The date of the spent transaction.
 * @property observations Any observations related to the spent transaction.
 * @property providerId The ID of the provider associated with the spent transaction.
 * @property providerName The name of the provider associated with the spent transaction.
 * @property spentId The ID of the spent transaction.
 */
data class SpentFB(
    val amount: Double,
    val created: String,
    val date: String,
    val observations: String,
    val providerId: String,
    val providerName: String,
    val spentId: String
) {
    /**
     * Returns a string representation of the spent transaction.
     */
    override fun toString(): String {
        return "Amount: $amount\nCreated: $created\nDate: $date\nObservations: $observations\nProviderId: $providerId\nProviderName: $providerName\nSpentId: $spentId"
    }

    /**
     * Converts the spent transaction to a string format suitable for export.
     */
    fun toExport(): String {
        val dateFormatted = date.transformDateIsoToString().substring(0, minOf(date.length, 10))
        val providerFormatted = providerName.substring(0, minOf(providerName.length, 18)).toUpperCamelCase()
        val amountFormatted = amount.euroFormat()
        return String.format("%-10s %-18s %10s", dateFormatted, providerFormatted, amountFormatted)
    }
}