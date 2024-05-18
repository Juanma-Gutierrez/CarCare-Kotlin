package com.juanmaGutierrez.carcare.model.localData

data class Spent(
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
}

