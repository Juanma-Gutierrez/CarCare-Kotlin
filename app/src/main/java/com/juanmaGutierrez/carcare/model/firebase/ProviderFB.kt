package com.juanmaGutierrez.carcare.model.firebase

import com.juanmaGutierrez.carcare.model.localData.Provider

data class ProviderFB(
    var providers: MutableList<Provider>,
) {
    override fun toString(): String {
        return "Providers: $providers"
    }
}