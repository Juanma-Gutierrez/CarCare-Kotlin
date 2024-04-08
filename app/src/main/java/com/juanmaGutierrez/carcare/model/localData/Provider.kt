package com.juanmaGutierrez.carcare.model.localData

data class Provider(
    var category: String,
    var created: String,
    var name: String,
    var phone: String,
    var providerId: String
)

data class Providers(
    var providers: List<Provider>
)

