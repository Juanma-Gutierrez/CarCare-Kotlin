package com.juanmaGutierrez.carcare.model.firebase

data class SpentFB(
    val amount: Int,
    val created: String,
    val date: String,
    val observations: String,
    val providerId: String,
    val providerName: String,
    val spentId: String
)