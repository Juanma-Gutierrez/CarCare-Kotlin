package com.juanmaGutierrez.carcare.model.localData

data class Provider(
    var category: String = "gasStation",
    var created: String = "",
    var name: String = "",
    var phone: String = "",
    var providerId: String = "",
) {
    override fun toString(): String {
        return "Category: $category\nCreated: $created\nName: $name\nPhone: $phone\nProviderId: $providerId\n"
    }
}

data class Providers(
    var providers: List<Provider>
) {
    override fun toString(): String {
        return "Providers: $providers"
    }
}


