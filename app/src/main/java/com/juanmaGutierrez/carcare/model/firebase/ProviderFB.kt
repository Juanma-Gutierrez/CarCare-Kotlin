package com.juanmaGutierrez.carcare.model.firebase

import com.juanmaGutierrez.carcare.model.localData.Provider

/**
 * Represents the Firebase data model for providers.
 * @property providers: List of providers.
 */
data class ProviderFB(
    var providers: MutableList<Provider>,
) {
    override fun toString(): String {
        return "Providers: $providers"
    }
}