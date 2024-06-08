package com.juanmaGutierrez.carcare.model.localData

/**
 * Represents a spent.
 * @property amount The amount spent.
 * @property created The date and time when the spent was created.
 * @property date The date of the spent.
 * @property observations Any observations related to the spent.
 * @property providerId The ID of the provider associated with the spent.
 * @property providerName The name of the provider associated with the spent.
 * @property spentId The ID of the spent.
 */
data class Spent(
    val amount: Double = 0.0,
    val created: String = "",
    var date: String = "",
    val observations: String = "",
    val providerId: String = "",
    val providerName: String = "",
    val spentId: String = ""
) {
    /**
     * Returns a string representation of the spent.
     */
    override fun toString(): String {
        return "Amount: $amount\nCreated: $created\nDate: $date\nObservations: $observations\nProviderId: $providerId\nProviderName: $providerName\nSpentId: $spentId"
    }
}

