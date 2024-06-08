package com.juanmaGutierrez.carcare.model.localData

import com.juanmaGutierrez.carcare.service.getTimestamp
import com.juanmaGutierrez.carcare.service.transformStringToDateIso

/**
 * Represents a provider entity.
 * @property category The category of the provider.
 * @property created The creation date of the provider.
 * @property name The name of the provider.
 * @property phone The phone number of the provider.
 * @property providerId The ID of the provider.
 */
data class Provider(
    var category: String = "gasStation",
    var created: String = getTimestamp().transformStringToDateIso(),
    var name: String = "",
    var phone: String = "",
    var providerId: String = "",
) {
    /**
     * Returns a string representation of the provider.
     */
    override fun toString(): String {
        return "Category: $category\nCreated: $created\nName: $name\nPhone: $phone\nProviderId: $providerId\n"
    }
}

/**
 * Represents a list of providers.
 * @property providers The list of providers.
 */
data class Providers(
    var providers: List<Provider>
) {
    /**
     * Returns a string representation of the provider.
     */
    override fun toString(): String {
        return "Providers: $providers"
    }
}