package com.juanmaGutierrez.carcare.model.localData

/**
 * Represents spent data grouped by provider for chart visualization.
 * @property providerName The name of the provider.
 * @property amount The total amount spent with the provider.
 */
data class SpentByProviderForChart(
    var providerName: String,
    var amount: Double
)
